package gov.uk.ets.registry.api.integration.metscontacts;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.integration.*;
import jakarta.persistence.EntityManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
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

import java.nio.charset.StandardCharsets;
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
                "kafka.integration.aviation.mets.contacts.enabled=true",
                "account.audit.enabled=false",
        }
)
@EmbeddedKafka(partitions = 1, topics = {
        "aviation-registry-contacts-request-topic",
        "registry-aviation-contacts-response-topic" })
class AviationMetsContactsEventConsumerIntegrationTest extends BaseIntegrationTest {

    static final String REQUEST_TOPIC =
            "aviation-registry-contacts-request-topic";
    private static final String RESPONSE_TOPIC =
            "registry-aviation-contacts-response-topic";

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

    private KafkaTemplate<String, MetsContactsEvent> kafkaTemplate;
    private Consumer<String, MetsContactsEventOutcome> responseConsumer;

    //Test data
    private TestAccountData data1;
    private TestAccountData data2;

    @BeforeAll
    void initData(@Autowired TestDataHelper helper) {
        data1 = dataFactory.nextAviationAccount();
        data2 = dataFactory.nextAviationAccount();

        helper.createAircraftOperatorAccountWithMetsContacts(data1);

        helper.createAircraftOperatorAccountWithMetsContacts(data2);

        // Producer
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);

        kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerProps,
                        new StringSerializer(),
                        new JsonSerializer<>()
                )
        );

        // Consumer with UNIQUE group id → no old messages
        JsonDeserializer<MetsContactsEventOutcome> deserializer =
                new JsonDeserializer<>(MetsContactsEventOutcome.class);
        deserializer.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("mets-contacts-test-" + UUID.randomUUID()),
                new StringDeserializer(),
                deserializer
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, RESPONSE_TOPIC);

        freemarkerConfiguration.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "/templates/email"
        );
    }


    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> cfg =
                new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        cfg.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return cfg;
    }

    @Test
    void shouldConsumeAviationMetsContactsEventAndAddingNewMetsContactsOverwritingExisting() {

        String correlationId = "corr-aviation-success";

        MetsContactsEvent event = eventHelper.aircraftMetsContactsEvent(data1.getOperatorIdMetsContacts());

        ProducerRecord<String, MetsContactsEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "aviation-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record);

        // Wait for DB update
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            Account updated = accountLoader.loadAccountWithRelations(data1.getAccountId());

            assertThat(updated).isNotNull();
            assertThat(updated.getMetsAccountContacts().size()).isEqualTo(2);
            assertThat(updated.getMetsAccountContacts())
                    .extracting(MetsAccountContact::getName)
                    .containsExactlyInAnyOrder(
                            "firstName2 lastName2",
                            "firstName1 lastName1"
                    );
            assertThat(updated.getMetsAccountContacts())
                    .extracting(MetsAccountContact::getName)
                    .doesNotContainAnyElementsOf(
                            List.of(TestDataHelper.METS_CONTACT_NAME_TO_OVERWRITE)
                    );
        });

        // Read response messages and filter by correlation-id
        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<MetsContactsEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, MetsContactsEventOutcome> rec : records) {
            Header header = rec.headers().lastHeader("Correlation-Id");
            if (header != null) {
                String corr = new String(header.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(corr)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).hasSize(1);

        var response = matching.get(0);
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    void shouldConsumeAviationMetsContactsEventAndNotAddingMetsContactsInCaseOfValidationErrors() {

        String correlationId = "corr-aviation-success";

        MetsContactsEvent event = eventHelper.aircraftMetsContactsEvent_invalid(data2.getOperatorIdMetsContacts());

        ProducerRecord<String, MetsContactsEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "aviation-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record);

        // Wait for DB update
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            Account updated = accountLoader.loadAccountWithRelations(data2.getAccountId());

            assertThat(updated).isNotNull();
            //Have one that already persist to be over write.
            assertThat(updated.getMetsAccountContacts().size()).isEqualTo(1);
        });

        // Read response messages and filter by correlation-id
        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<MetsContactsEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, MetsContactsEventOutcome> rec : records) {
            Header header = rec.headers().lastHeader("Correlation-Id");
            if (header != null) {
                String corr = new String(header.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(corr)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).hasSize(1);

        var response = matching.get(0);
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors().size()).isEqualTo(9);
    }
}
