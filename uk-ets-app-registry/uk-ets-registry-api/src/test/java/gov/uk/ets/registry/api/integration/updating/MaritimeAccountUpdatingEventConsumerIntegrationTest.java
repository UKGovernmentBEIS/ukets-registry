package gov.uk.ets.registry.api.integration.updating;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.integration.AccountEventTestHelper;
import gov.uk.ets.registry.api.helper.integration.AccountLoader;
import gov.uk.ets.registry.api.helper.integration.TestDataHelper;
import jakarta.persistence.EntityManager;
import gov.uk.ets.registry.api.helper.integration.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestPropertySource(
        locations = "/integration-test-application.properties",
        properties = {
                "kafka.integration.enabled=true",
                "kafka.integration.maritime.account.updating.enabled=true",
                "account.audit.enabled=false",
                "logging.level.root=ERROR"
        }
)
@EmbeddedKafka(partitions = 1, topics = { "registry-mrtm-account-updated-request-topic" , "mrtm-registry-account-updated-response-topic" })
class MaritimeAccountUpdatingEventConsumerIntegrationTest extends BaseIntegrationTest {

    private static final String REQUEST_TOPIC =
            "registry-mrtm-account-updated-request-topic";

    private static final String RESPONSE_TOPIC =
            "mrtm-registry-account-updated-response-topic";

    @Autowired private EntityManager entityManager;
    @Autowired private AccountLoader accountLoader;
    @Autowired private AccountEventTestHelper eventHelper;
    @Autowired private TestAccountDataFactory dataFactory;
    @Autowired private Configuration freemarkerConfiguration;
    @Autowired private IntegrationTestsKafkaHelper  kafkaHelper;

    private KafkaTemplate<String, AccountUpdatingEvent> kafkaTemplate;
    private Consumer<String, AccountUpdatingEventOutcome> responseConsumer;

    //Test data
    private TestAccountData data1;
    @Autowired
    private TestDataHelper testDataHelper;

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

        helper.createMaritimeOperatorAccount(data1);


        // Producer
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new JsonSerializer<>())
        );

        // Consumer with UNIQUE group id so we don't see old messages
        JsonDeserializer<AccountUpdatingEventOutcome> deser =
                new JsonDeserializer<>(AccountUpdatingEventOutcome.class);
        deser.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("maritime-test-" + UUID.randomUUID()),
                new StringDeserializer(),
                deser
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, RESPONSE_TOPIC);

        freemarkerConfiguration.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "/templates/email"
        );
    }

    // ---------------------------------------------------------------------
    // SUCCESS CASE
    // ---------------------------------------------------------------------
    @Test
    void shouldConsumeMaritimeEventAndUpdateAccount() throws Exception {

        String corrId = "corr-maritime-success";

        AccountUpdatingEvent event = eventHelper.buildMaritimeSuccessUpdateEvent(
                data1.getRegistryId(),
                null,
                "IMO-1234567",
                "Updated Maritime Holder"
        );

        ProducerRecord<String, AccountUpdatingEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "maritime-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", corrId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        // Wait for DB update
        await().atMost(25, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();
            Account updated = accountLoader.loadAccountWithRelations(data1.getAccountId());

            assertThat(updated).isNotNull();
            assertThat(updated.getAccountHolder().getName())
                    .isEqualTo("Updated Maritime Holder");
            MaritimeOperator operator = (MaritimeOperator) updated.getCompliantEntity();
            assertThat(operator.getMaritimeMonitoringPlanIdentifier()).isEqualTo("10000001");
        });

        // Read all responses, then match by correlation-id
        var records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        AccountUpdatingEventOutcome response =
                kafkaHelper.extractByCorrelation(records, corrId);

        assertThat(response)
                .as("Expected a response matching correlation ID " + corrId)
                .isNotNull();

        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();
    }

    // ---------------------------------------------------------------------
    // VALIDATION ERRORS CASE
    // ---------------------------------------------------------------------

    @Test
    void shouldProduceValidationErrorsForInvalidMaritimeEvent() throws Exception {

        String corrId = "corr-maritime-invalid";

        AccountUpdatingEvent invalidEvent =
                eventHelper.buildInvalidMaritimeUpdateEventWithValidationErrors();

        ProducerRecord<String, AccountUpdatingEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "maritime-key",
                        invalidEvent,
                        List.of(new RecordHeader("Correlation-Id", corrId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(15));

        AccountUpdatingEventOutcome outcome =
                kafkaHelper.extractByCorrelation(records, corrId);

        assertThat(outcome)
                .as("Expected an ERROR outcome for correlation ID " + corrId)
                .isNotNull();

        assertThat(outcome.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(outcome.getErrors()).isNotEmpty();

        var codes = outcome.getErrors().stream()
                .map(e -> e.getError().name())
                .collect(java.util.stream.Collectors.toSet());

        assertThat(codes).containsAnyOf(
                "ERROR_0317", // invalid registry ID
                "ERROR_0316", // invalid service
                "ERROR_0319", // duplicate/invalid monitoring plan / IMO
                "ERROR_0106", // invalid first year of verified emissions
                "ERROR_0303"  // missing mandatory field
        );
    }

    @Test
    void shouldReturnBoth0319And0310WhenMonitoringPlanAndIMOAlreadyExist() {
        TestAccountData mpData = dataFactory.nextMaritimeAccount();
        String duplicateMP = "MP-DUP-" + mpData.getOperatorId();

        testDataHelper.createMaritimeAccountWithMonitoringPlan(
                mpData,
                duplicateMP
        );

        TestAccountData imoData = dataFactory.nextMaritimeAccount();
        String duplicateIMO = "IMO-" + imoData.getOperatorId();

        testDataHelper.createMaritimeAccountWithIMO(
                imoData,
                duplicateIMO
        );

        String corrId = "corr-dupe-both";

        AccountUpdatingEvent event =
                eventHelper.buildMaritimeSuccessUpdateEvent(
                        data1.getRegistryId(),
                        duplicateMP,     // duplicate monitoring plan → ERROR_0319
                        duplicateIMO,    // duplicate IMO → ERROR_0310
                        "Holder X"
                );

        kafkaHelper.sendKafkaRequest(REQUEST_TOPIC, event, corrId, kafkaTemplate);
        AccountUpdatingEventOutcome outcome = kafkaHelper.receiveOutcome(responseConsumer,corrId);

        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0319"));

        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0310"));
    }
}
