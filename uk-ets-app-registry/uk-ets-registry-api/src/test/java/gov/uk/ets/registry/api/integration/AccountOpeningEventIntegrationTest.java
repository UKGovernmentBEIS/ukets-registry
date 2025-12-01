package gov.uk.ets.registry.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.integration.service.account.AccountEventValidator;
import gov.uk.ets.registry.api.notification.EmailNotification;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;

@TestPropertySource(locations = "/integration-test-application.properties", properties = {
	"kafka.integration.enabled=true",
    "kafka.integration.installation.account.opening.enabled=true",
    "kafka.integration.aviation.account.opening.enabled=true",
    "kafka.integration.maritime.account.opening.enabled=true",
    "kafka.integration.installation.set.operator.enabled=true",
    "kafka.integration.aviation.set.operator.enabled=true",
    "kafka.integration.maritime.set.operator.enabled=true"
})
@Disabled
class AccountOpeningEventIntegrationTest extends BaseIntegrationTest {

    private static final String ACCOUNT_OPENING_INSTALLATION_TOPIC = "installation-account-created-request-topic";
    private static final String ACCOUNT_OPENING_AVIATION_TOPIC = "aviation-account-created-request-topic";
    private static final String ACCOUNT_OPENING_MARITIME_TOPIC = "maritime-account-created-request-topic";

    @Autowired
    private Configuration freemarkerConfiguration;

    @MockitoSpyBean
    private AccountEventValidator validator;

    private KafkaTemplate<String, AccountOpeningEvent> kafkaTemplate;

    private Consumer<String, AccountOpeningEventOutcome> installationResponseConsumer;
    private Consumer<String, AccountOpeningEventOutcome> aviationResponseConsumer;
    private Consumer<String, AccountOpeningEventOutcome> maritimeResponseConsumer;
    private Consumer<String, EmailNotification> emailConsumer;
    private Consumer<String, OperatorUpdateEvent> setOperatorConsumer;
    private Consumer<String, String> dltConsumer;

    @BeforeAll
    public void init() {
        // Setup kafka template
        DefaultKafkaProducerFactory<String, AccountOpeningEvent> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new JsonSerializer<>());
        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Setup kafka consumers
        JsonDeserializer<AccountOpeningEventOutcome> deserializer = new JsonDeserializer<>(AccountOpeningEventOutcome.class);
        installationResponseConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("installation-response-group"), new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(installationResponseConsumer, "installation-account-created-response-topic");

        aviationResponseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("aviation-response-group"), new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(aviationResponseConsumer, "aviation-account-created-response-topic");

        maritimeResponseConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("maritime-response-group"), new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(maritimeResponseConsumer, "maritime-account-created-response-topic");

        JsonDeserializer<EmailNotification> emailDeserializer = new JsonDeserializer<>(EmailNotification.class);
        emailConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("email-group"), new StringDeserializer(), emailDeserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(emailConsumer, "group.notification.topic");

        JsonDeserializer<OperatorUpdateEvent> operatorEventDeserializer = new JsonDeserializer<>(OperatorUpdateEvent.class);
        setOperatorConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("set-operator-group"), new StringDeserializer(), operatorEventDeserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(setOperatorConsumer, "maritime-set-operator-request-topic");

        dltConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("dlt-group"), new StringDeserializer(),
            new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dltConsumer, ACCOUNT_OPENING_MARITIME_TOPIC + "-dlt");
    }

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> config =
            new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        // make sure that we read transactionally committed messages
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // make sure that we do not read old messages in case of re-balancing
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return config;
    }

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates/email");
    }

    @AfterEach
    protected void cleanUp() {
        installationResponseConsumer.commitSync();
        aviationResponseConsumer.commitSync();
        maritimeResponseConsumer.commitSync();
        emailConsumer.commitSync();
        setOperatorConsumer.commitSync();
        dltConsumer.commitSync();
    }

    @AfterAll
    protected void tearDown() {
        installationResponseConsumer.close();
        aviationResponseConsumer.close();
        maritimeResponseConsumer.close();
        dltConsumer.close();
        emailConsumer.close();
        setOperatorConsumer.close();
        super.tearDown();
    }

    @Test
    void testAccountOpeningInstallationEvent() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setInstallationName("Installation name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setPermitId("3214567");
        accountDetailsMessage.setInstallationActivityTypes(Set.of(InstallationActivityType.MANUFACTURE_OF_CERAMICS.name()));
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountOpeningEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_OPENING_INSTALLATION_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
            records = KafkaTestUtils.getRecords(installationResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        ConsumerRecords<String, EmailNotification>
            emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
        assertThat(emailRecords.iterator().next().value().getSubject())
            .contains("A new account", "has been created");
    }

    @Test
    void testAccountOpeningAircraftOperatorEvent() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("account name");
        accountDetailsMessage.setMonitoringPlanId("monitoringPlanId");
        accountDetailsMessage.setEmitterId("32145678");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2024);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("GR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountOpeningEvent> providedEvent =
                new ProducerRecord<>(ACCOUNT_OPENING_AVIATION_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
                records = KafkaTestUtils.getRecords(aviationResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        ConsumerRecords<String, EmailNotification>
                emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
        assertThat(emailRecords.iterator().next().value().getSubject())
                .contains("A new account", "has been created");
    }

    @Test
    void testAccountOpeningMaritimeEventWithOrganisationAccountHolder() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("maritime account name");
        accountDetailsMessage.setEmitterId("O3214567");
        accountDetailsMessage.setCompanyImoNumber("IMO number ORG");
        accountDetailsMessage.setMonitoringPlanId("Monitoring plan id ORG");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode("123456");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountOpeningEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_OPENING_MARITIME_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
            records = KafkaTestUtils.getRecords(maritimeResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        ConsumerRecords<String, EmailNotification>
            emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
        assertThat(emailRecords.iterator().next().value().getSubject())
            .contains("A new account", "has been created");

        ConsumerRecords<String, OperatorUpdateEvent>
            setOperatorRecords = KafkaTestUtils.getRecords(setOperatorConsumer, Duration.ofMillis(10000), 1);
        assertThat(setOperatorRecords.count()).isEqualTo(1);
        ConsumerRecord<String, OperatorUpdateEvent> operatorRecord = setOperatorRecords.iterator().next();
        assertThat(operatorRecord.value().getEmitterId()).isEqualTo("O3214567");
        assertThat(operatorRecord.value().getOperatorId()).isNotNull();
        assertThat(operatorRecord.headers().lastHeader("Correlation-Id").value()).isNotEmpty();
    }

    @Test
    void testAccountOpeningMaritimeEventWithIndividualAccountHolder() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("maritime account name");
        accountDetailsMessage.setEmitterId("I3214567");
        accountDetailsMessage.setCompanyImoNumber("IMO number IND");
        accountDetailsMessage.setMonitoringPlanId("Monitoring plan id IND");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2026);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("INDIVIDUAL");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("UK");
        accountHolderMessage.setPostalCode("123456");
        event.setAccountHolder(accountHolderMessage);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountOpeningEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_OPENING_MARITIME_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
            records = KafkaTestUtils.getRecords(maritimeResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        ConsumerRecords<String, EmailNotification>
            emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
        assertThat(emailRecords.iterator().next().value().getSubject())
            .contains("A new account", "has been created");

        ConsumerRecords<String, OperatorUpdateEvent>
            setOperatorRecords = KafkaTestUtils.getRecords(setOperatorConsumer, Duration.ofMillis(10000), 1);
        assertThat(setOperatorRecords.count()).isEqualTo(1);
        ConsumerRecord<String, OperatorUpdateEvent> operatorRecord = setOperatorRecords.iterator().next();
        assertThat(operatorRecord.value().getEmitterId()).isEqualTo("I3214567");
        assertThat(operatorRecord.value().getOperatorId()).isNotNull();
        assertThat(operatorRecord.headers().lastHeader("Correlation-Id").value()).isNotEmpty();
    }

    @Test
    void testAccountOpeningEventWithBusinessError() {
        // given
        AccountOpeningEvent event = new AccountOpeningEvent();
        AccountDetailsMessage accountDetailsMessage = new AccountDetailsMessage();
        accountDetailsMessage.setAccountName("maritime account name");
        accountDetailsMessage.setEmitterId("3214567");
        accountDetailsMessage.setCompanyImoNumber("IMO number");
        accountDetailsMessage.setMonitoringPlanId("Monitoring plan id");
        accountDetailsMessage.setRegulator("EA");
        accountDetailsMessage.setFirstYearOfVerifiedEmissions(2000);
        event.setAccountDetails(accountDetailsMessage);

        AccountHolderMessage accountHolderMessage = new AccountHolderMessage();
        accountHolderMessage.setAccountHolderType("ORGANISATION");
        accountHolderMessage.setName("account holder name");
        accountHolderMessage.setAddressLine1("address");
        accountHolderMessage.setTownOrCity("town");
        accountHolderMessage.setCountry("FR");
        accountHolderMessage.setCrnNotExist(false);
        accountHolderMessage.setCompanyRegistrationNumber("Registration Number");
        event.setAccountHolder(accountHolderMessage);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountOpeningEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_OPENING_MARITIME_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
            records = KafkaTestUtils.getRecords(maritimeResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors()).isNotEmpty();

        ConsumerRecords<String, EmailNotification>
            emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
        assertThat(emailRecords.iterator().next().value().getSubject())
            .isEqualTo("*ACTION REQUIRED* - Integration Point: OPEN_ACCOUNT - Emitter ID: 3214567 - Error Code(s): ERROR_0106");
    }

    @Test
    void testAccountOpeningEventUnexpectedError() {
        // given
        DefaultKafkaProducerFactory<String, String> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new StringSerializer());
        KafkaTemplate<String, String> stringKafkaTemplate = new KafkaTemplate<>(producerFactory);

        doThrow(new RuntimeException()).when(validator).validate(any(AccountOpeningEvent.class), eq(OperatorType.INSTALLATION));

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, String> providedEvent =
            new ProducerRecord<>(ACCOUNT_OPENING_MARITIME_TOPIC, null, null, "1", "{}", List.of(recordHeader));

        // when
        stringKafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, AccountOpeningEventOutcome>
            records = KafkaTestUtils.getRecords(maritimeResponseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountOpeningEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountOpeningEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors().get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0100);
    }

    @Test
    void testAccountOpeningEventInvalidMessages() {
        // given
        DefaultKafkaProducerFactory<String, String> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new StringSerializer());
        KafkaTemplate<String, String> stringKafkaTemplate = new KafkaTemplate<>(producerFactory);

        // when
        stringKafkaTemplate.send(ACCOUNT_OPENING_MARITIME_TOPIC, "test");
        stringKafkaTemplate.flush();

        // then
        ConsumerRecords<String, String>
            records = KafkaTestUtils.getRecords(dltConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
        ConsumerRecord<String, String> invalidFormat = iterator.next();
        assertThat(invalidFormat.key()).isNull();
        assertThat(invalidFormat.value()).isEqualTo("test");
    }
}
