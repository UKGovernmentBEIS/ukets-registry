package gov.uk.ets.registry.api.integration.metscontacts;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.integration.*;
import gov.uk.ets.registry.api.integration.updating.IntegrationTestsKafkaHelper;
import jakarta.persistence.EntityManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static gov.uk.ets.registry.api.helper.integration.MetsContactsTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestPropertySource(
        locations = "/integration-test-application.properties",
        properties = {
                "kafka.integration.enabled=true",
                "kafka.integration.maritime.mets.contacts.enabled=true",
                "logging.level.root=ERROR",
                "account.audit.enabled=false",
        }
)
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "mrtm-registry-contacts-request-topic",
                "registry-mrtm-contacts-response-topic"
        }
)
class MaritimeMetsContactsEventConsumerIntegrationTest extends BaseIntegrationTest {

    private static final String REQUEST_TOPIC =
            "mrtm-registry-contacts-request-topic";

    private static final String RESPONSE_TOPIC =
            "registry-mrtm-contacts-response-topic";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AccountLoader accountLoader;

    @Autowired
    private MetsContactsTestHelper eventHelper;

    @Autowired
    private TestAccountDataFactory dataFactory;

    @Autowired
    private Configuration freemarkerConfiguration;

    @Autowired
    private MetsContactsIntegrationTestsKafkaHelper kafkaHelper;

    @Autowired
    private TestDataHelper testDataHelper;

    private KafkaTemplate<String, MetsContactsEvent> kafkaTemplate;
    private Consumer<String, MetsContactsEventOutcome> responseConsumer;

    private TestAccountData data1;
    private TestAccountData data2;

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> cfg =
                new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        cfg.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return cfg;
    }

    @BeforeAll
    void initData(@Autowired TestDataHelper helper) {

        data1 = dataFactory.nextMaritimeAccount();
        data2 = dataFactory.nextMaritimeAccount();

        helper.createMaritimeAccountWithMetsContacts(data1);
        helper.createMaritimeAccountWithMetsContacts(data2);

        // Kafka producer
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerProps,
                        new StringSerializer(),
                        new JsonSerializer<>()
                )
        );

        // Kafka consumer
        JsonDeserializer<MetsContactsEventOutcome> deserializer =
                new JsonDeserializer<>(MetsContactsEventOutcome.class);
        deserializer.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("mets-contacts-test-" + UUID.randomUUID()),
                new StringDeserializer(),
                deserializer
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(
                responseConsumer, RESPONSE_TOPIC
        );

        freemarkerConfiguration.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "/templates/email"
        );
    }

    // ---------------------------------------------------------------------
    // SUCCESS CASE
    // ---------------------------------------------------------------------
    @Test
    void shouldConsumeMaritimeMetsContactsEventAndOverwriteExistingContacts()
            throws Exception {

        String corrId = "corr-mets-success";

        MetsContactsEvent event =
                eventHelper.maritimeMetsContactsEvent(
                        data1.getOperatorIdMetsContacts()
                );

        ProducerRecord<String, MetsContactsEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "mets-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", corrId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        // DB assertions
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            Account updated =
                    accountLoader.loadAccountWithRelations(data1.getAccountId());

            assertThat(updated).isNotNull();
            assertThat(updated.getMetsAccountContacts()).hasSize(2);

            assertThat(updated.getMetsAccountContacts())
                    .extracting(MetsAccountContact::getName)
                    .containsExactlyInAnyOrder(
                            "firstName1 lastName1",
                            "firstName2 lastName2"
                    );

            assertThat(updated.getMetsAccountContacts())
                    .extracting(MetsAccountContact::getName)
                    .doesNotContain(
                            TestDataHelper.METS_CONTACT_NAME_TO_OVERWRITE
                    );
        });

        // Kafka response
        MetsContactsEventOutcome response =
                kafkaHelper.receiveOutcome(responseConsumer, corrId);

        assertThat(response).isNotNull();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();
    }

    // ---------------------------------------------------------------------
    // VALIDATION ERROR CASE
    // ---------------------------------------------------------------------
    @Test
    void shouldNotOverwriteContactsWhenValidationErrorsExist()
            throws Exception {

        String corrId = "corr-mets-invalid";

        MetsContactsEvent invalidEvent =
                eventHelper.maritimeMetsContactsEvent_invalid(
                        data2.getOperatorIdMetsContacts()
                );

        ProducerRecord<String, MetsContactsEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "mets-key",
                        invalidEvent,
                        List.of(new RecordHeader("Correlation-Id", corrId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        // DB should remain unchanged
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            Account updated =
                    accountLoader.loadAccountWithRelations(data2.getAccountId());

            assertThat(updated).isNotNull();
            assertThat(updated.getMetsAccountContacts()).hasSize(1);
        });

        // Kafka response
        MetsContactsEventOutcome response =
                kafkaHelper.receiveOutcome(responseConsumer, corrId);

        assertThat(response).isNotNull();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors().size()).isGreaterThan(0);
    }
}
