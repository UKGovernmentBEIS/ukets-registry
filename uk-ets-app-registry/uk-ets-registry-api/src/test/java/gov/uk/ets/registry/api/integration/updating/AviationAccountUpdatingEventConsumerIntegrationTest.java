package gov.uk.ets.registry.api.integration.updating;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.integration.AccountEventTestHelper;
import gov.uk.ets.registry.api.helper.integration.AccountLoader;
import gov.uk.ets.registry.api.helper.integration.TestDataHelper;
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
import org.junit.jupiter.api.*;
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
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestPropertySource(
        locations = "/integration-test-application.properties",
        properties = {
                "kafka.integration.enabled=true",
                "kafka.integration.aviation.account.updating.enabled=true",
                "account.audit.enabled=false",
                "logging.level.root=ERROR"
        }
)
@EmbeddedKafka(partitions = 1, topics = { "registry-aviation-account-updated-request-topic" , "registry-aviation-account-updated-response-topic" })
class AviationAccountUpdatingEventConsumerIntegrationTest extends BaseIntegrationTest {

    private static final String REQUEST_TOPIC =
            "registry-aviation-account-updated-request-topic";

    private static final String RESPONSE_TOPIC =
            "registry-aviation-account-updated-response-topic";

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AccountLoader accountLoader;
    @Autowired
    private AccountEventTestHelper eventHelper;
    @Autowired
    private TestAccountDataFactory dataFactory;
    @Autowired
    private Configuration freemarkerConfiguration;

    private KafkaTemplate<String, AccountUpdatingEvent> kafkaTemplate;
    private Consumer<String, AccountUpdatingEventOutcome> responseConsumer;

    //Test data
    private TestAccountData data1;
    @BeforeAll
    void initData(@Autowired TestDataHelper helper) {
        data1 = dataFactory.nextAviationAccount();

        helper.createAircraftOperatorAccount(data1);

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
        JsonDeserializer<AccountUpdatingEventOutcome> deserializer =
                new JsonDeserializer<>(AccountUpdatingEventOutcome.class);
        deserializer.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("aviation-update-test-" + UUID.randomUUID()),
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
    void shouldConsumeAviationEventAndUpdateAccount() throws Exception {

        String correlationId = "corr-aviation-success";

        AccountUpdatingEvent event =
                eventHelper.buildAircraftSuccessUpdateEvent(
                        data1.getRegistryId(),
                        null,
                        "Updated Holder Aviation"
                );

        ProducerRecord<String, AccountUpdatingEvent> record =
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
            assertThat(updated.getAccountHolder().getName())
                    .isEqualTo("Updated Holder Aviation");

            AircraftOperator operator = (AircraftOperator) updated.getCompliantEntity();
            assertThat(operator.getMonitoringPlanIdentifier()).isEqualTo("654322");
        });

        // Read response messages and filter by correlation-id
        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<AccountUpdatingEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, AccountUpdatingEventOutcome> rec : records) {
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
    void shouldProduceValidationErrorsWhenInvalidAviationEventReceived() throws Exception {

        String correlationId = "corr-invalid-aviation";

        AccountUpdatingEvent invalidEvent =
                eventHelper.buildInvalidAircraftUpdateEventWithValidationErrors();

        ProducerRecord<String, AccountUpdatingEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "aviation-key",
                        invalidEvent,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(15));

        List<AccountUpdatingEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, AccountUpdatingEventOutcome> rec : records) {
            Header header = rec.headers().lastHeader("Correlation-Id");
            if (header != null) {
                String corr = new String(header.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(corr)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).isNotEmpty();
        AccountUpdatingEventOutcome outcome = matching.get(0);

        assertThat(outcome.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(outcome.getErrors()).isNotEmpty();

        var codes = outcome.getErrors().stream()
                .map(e -> e.getError().name())
                .collect(java.util.stream.Collectors.toSet());

        assertThat(codes).containsAnyOf(
                "ERROR_0317", // registry id
                "ERROR_0316", // invalid service
                "ERROR_0303", // missing mandatory field
                "ERROR_0311", // invalid year
                "ERROR_0319"  // monitoring plan validation / uniqueness
        );
    }
}
