package gov.uk.ets.registry.api.integration.updating;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.awaitility.Awaitility.await;

@TestPropertySource(
        locations = "/integration-test-application.properties",
        properties = {
                "kafka.integration.enabled=true",
                "kafka.integration.installation.account.updating.enabled=true",
                "account.audit.enabled=false",
                "logging.level.root=ERROR"
        }
)
@EmbeddedKafka(partitions = 1, topics = {
        "registry-installation-account-updated-request-topic-dlt",
        "registry-installation-account-updated-request-topic" ,
        "registry-installation-account-updated-response-topic" })
class InstallationAccountUpdatingEventConsumerIntegrationTest extends BaseIntegrationTest {

    private static final String INSTALLATION_REQUEST_TOPIC =
            "registry-installation-account-updated-request-topic";

    private static final String INSTALLATION_RESPONSE_TOPIC =
            "registry-installation-account-updated-response-topic";

    @Autowired private EntityManager entityManager;
    @Autowired private AccountLoader accountLoader;
    @Autowired private TestDataHelper testDataHelper;
    @Autowired private AccountEventTestHelper eventHelper;
    @Autowired private TestAccountDataFactory dataFactory;
    @Autowired private Configuration freemarkerConfiguration;
    @Autowired private IntegrationTestsKafkaHelper kafkaHelper;

    private KafkaTemplate<String, AccountUpdatingEvent> kafkaTemplate;
    private Consumer<String, AccountUpdatingEventOutcome> responseConsumer;

    //Test data
    private TestAccountData data1;
    private TestAccountData closedAccountData;

    @BeforeAll
    void initTestData() {
        data1 = dataFactory.nextInstallationAccount();
        closedAccountData = dataFactory.nextInstallationAccount();

        testDataHelper.createInstallationAccount(data1);
        testDataHelper.createClosedInstallationAccount(closedAccountData);

        // Producer
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerProps,
                        new StringSerializer(),
                        new JsonSerializer<>()
                )
        );

        // Consumer with UNIQUE group ID
        JsonDeserializer<AccountUpdatingEventOutcome> deserializer =
                new JsonDeserializer<>(AccountUpdatingEventOutcome.class);
        deserializer.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("installation-update-" + UUID.randomUUID()),
                new StringDeserializer(),
                deserializer
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, INSTALLATION_RESPONSE_TOPIC);

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
    void shouldConsumeInstallationEventAndUpdateAccount() throws Exception {

        String correlationId = "corr-install-success";

        AccountUpdatingEvent event =
                eventHelper.buildInstallationSuccessUpdateEvent(
                        data1.getRegistryId(),
                        "PERMIT-123",
                        "Integration Test Installation",
                        "Updated Installation Holder"
                );

        ProducerRecord<String, AccountUpdatingEvent> record =
                new ProducerRecord<>(
                        INSTALLATION_REQUEST_TOPIC,
                        null,
                        null,
                        "install-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        // DB state verification
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();
            Account updated = accountLoader.loadAccountWithRelations(data1.getAccountId());

            assertThat(updated.getAccountHolder().getName())
                    .isEqualTo("Updated Installation Holder");

            Installation installation = (Installation) updated.getCompliantEntity();
            assertThat(installation.getInstallationName())
                    .isEqualTo("Integration Test Installation");
            assertThat(installation.getRegulator()).isEqualTo(RegulatorType.OPRED);
            assertThat(installation.getActivityTypes().stream().map(t->t.getDescription()).collect(Collectors.toSet()))
            .isEqualTo(Set.of(InstallationActivityType.COMBUSTION_OF_FUELS.toString()));
        });

        // Response filtering by correlation ID
        var records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<AccountUpdatingEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, AccountUpdatingEventOutcome> rec : records) {
            Header h = rec.headers().lastHeader("Correlation-Id");
            if (h != null) {
                String cid = new String(h.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(cid)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).hasSize(1);
        assertThat(matching.get(0).getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
    }

    @Test
    void shouldProduceValidationErrorsForInvalidInstallationEvent() throws Exception {

        String correlationId = "corr-install-invalid";

        AccountUpdatingEvent invalid =
                eventHelper.buildInvalidInstallationUpdateEventWithValidationErrors();

        ProducerRecord<String, AccountUpdatingEvent> record =
                new ProducerRecord<>(
                        INSTALLATION_REQUEST_TOPIC,
                        null,
                        null,
                        "install-key",
                        invalid,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);

        var records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<AccountUpdatingEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, AccountUpdatingEventOutcome> rec : records) {
            Header h = rec.headers().lastHeader("Correlation-Id");
            if (h != null) {
                String cid = new String(h.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(cid)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).isNotEmpty();

        AccountUpdatingEventOutcome outcome = matching.get(0);

        assertThat(outcome.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(outcome.getErrors()).isNotEmpty();
    }

    @Test
    void shouldReturn0313WhenInstallationAccountStatusClosed() {

        String corrId = "corr-installation-closed";

        AccountUpdatingEvent ev =
                eventHelper.buildInstallationSuccessUpdateEvent(
                        closedAccountData.getRegistryId(),
                        "PERMIT-CLOSED",
                        "Closed Installation",
                        "Holder X"
                );

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);
        AccountUpdatingEventOutcome outcome = kafkaHelper.receiveOutcome(responseConsumer,corrId);

        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0313"));
    }

    @Test
    void shouldReturn0314WhenTryingToChangeAccountHolderTypeForOpenAccount() {

        String corrId = "corr-holder-type-change";

        // Build a normal valid update event
        AccountUpdatingEvent ev =
                eventHelper.buildInstallationSuccessUpdateEvent(
                        data1.getRegistryId(),
                        "PERMIT-ORG-TO-IND",
                        "Installation X",
                        "Holder X"
                );

        // Try changing ORGANISATION → INDIVIDUAL
        ev.getAccountHolder().setAccountHolderType("INDIVIDUAL");

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);
        AccountUpdatingEventOutcome outcome = kafkaHelper.receiveOutcome(responseConsumer, corrId);

        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0314"));
    }

    @Test
    void shouldReturn0311WhenTryingToSetYearBefore2021() {

        String corrId = "corr-holder-type-change";

        // Build a normal valid update event
        AccountUpdatingEvent ev =
                eventHelper.buildInstallationSuccessUpdateEvent(
                        data1.getRegistryId(),
                        "PERMIT-ORG-TO-IND",
                        "Installation X",
                        "Holder X"
                );

        // Try changing ORGANISATION → INDIVIDUAL
        ev.getAccountDetails().setFirstYearOfVerifiedEmissions(2015);

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);
        AccountUpdatingEventOutcome outcome = kafkaHelper.receiveOutcome(responseConsumer, corrId);

        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0311"));
    }

    @Test
    void shouldReturn0315_WhenFYVEAfterExcludedYear() {

        String corrId = "corr-0315-excluded";
        TestAccountData data2 = dataFactory.nextInstallationAccount();

        testDataHelper.createInstallationAccount(data2);

        // GIVEN DB has excluded emissions for 2023
        testDataHelper.addExcludedEmissionsEntry(
                data2.getOperatorId(),
                2023
        );

        // WHEN
        AccountUpdatingEvent ev =
                eventHelper.buildFYVEUpdateEvent_ExcludedYear(
                        data2.getRegistryId(),
                        2024
                );

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);

        AccountUpdatingEventOutcome outcome =
                kafkaHelper.receiveOutcome(responseConsumer, corrId);

        // THEN
        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0315"));
    }

    @Test
    void shouldReturn0315_WhenFYVEAfterYearWithEmissions() {

        String corrId = "corr-0315-emissions";

        TestAccountData data3 = dataFactory.nextInstallationAccount();
        testDataHelper.createInstallationAccount(data3);
        // GIVEN DB has emissions for 2023
        testDataHelper.addEmissionsEntry(
                data3.getOperatorId(),
                2023,
                15L    // emissions value
        );

        // WHEN
        AccountUpdatingEvent ev =
                eventHelper.buildFYVEUpdateEvent_AfterEmissions(
                        data3.getRegistryId(),
                        2024
                );

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);

        AccountUpdatingEventOutcome outcome =
                kafkaHelper.receiveOutcome(responseConsumer, corrId);

        // THEN
        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0315"));
    }

    @Test
    void shouldReturn0315_WhenFYVEAfterYearWithAllocations() {

        String corrId = "corr-0315-allocations";
        TestAccountData data4 = dataFactory.nextInstallationAccount();

        Account installationAccount = testDataHelper.createInstallationAccount(data4);
        Long compliantEntityId = installationAccount.getCompliantEntity().getIdentifier();

        // GIVEN DB has "allocation-like" excluded entry for 2023
        testDataHelper.addExcludedEmissionsEntry(
                compliantEntityId,
                2023
        );

        // WHEN
        AccountUpdatingEvent ev =
                eventHelper.buildFYVEUpdateEvent_AfterAllocations(
                        data4.getRegistryId(),
                        2024
                );

        kafkaHelper.sendKafkaRequest(INSTALLATION_REQUEST_TOPIC, ev, corrId, kafkaTemplate);

        AccountUpdatingEventOutcome outcome =
                kafkaHelper.receiveOutcome(responseConsumer, corrId);

        // THEN
        assertThat(outcome.getErrors())
                .anyMatch(e -> e.getError().name().equals("ERROR_0315"));
    }
}
