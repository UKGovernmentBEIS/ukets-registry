package gov.uk.ets.registry.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutbox;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutboxRepository;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutboxStatus;
import gov.uk.ets.registry.api.file.upload.emissionstable.messaging.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.integration.message.AccountEmissionsUpdateEvent;
import gov.uk.ets.registry.api.integration.message.AccountEmissionsUpdateEventOutcome;
import gov.uk.ets.registry.api.integration.message.IntegrationEventOutcome;
import gov.uk.ets.registry.api.integration.service.emission.EmissionEventValidator;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.time.Duration;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "kafka.integration.enabled=true"
})
class EmissionEventIntegrationTest extends BaseIntegrationTest {

    private static final String ACCOUNT_EMISSIONS_UPDATED_TOPIC = "installation-account-emissions-updated-request-topic";
    private KafkaTemplate<String, AccountEmissionsUpdateEvent> kafkaTemplate;

    @Autowired
    private ComplianceOutboxRepository repository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Configuration freemarkerConfiguration;

    @SpyBean
    private EmissionEventValidator validator;

    private Consumer<String, String> dltConsumer;
    private Consumer<String, AccountEmissionsUpdateEventOutcome> responseConsumer;
    private Consumer<String, EmailNotification> emailConsumer;
    private Installation installation;
    private Account account;

    @BeforeAll
    public void init() {
        // Clear duplicate assignments
        List<Account> accounts = accountRepository.findAll();
        accounts.forEach(a -> a.setCompliantEntity(null));
        accountRepository.saveAllAndFlush(accounts);

        // Setup kafka template
        DefaultKafkaProducerFactory<String, AccountEmissionsUpdateEvent> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new JsonSerializer<>());
        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Setup kafka consumers
        dltConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("dlt-group"), new StringDeserializer(),
            new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dltConsumer, ACCOUNT_EMISSIONS_UPDATED_TOPIC + "-dlt");

        JsonDeserializer<AccountEmissionsUpdateEventOutcome> deserializer = new JsonDeserializer<>(AccountEmissionsUpdateEventOutcome.class);
        responseConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("response-group"), new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, "installation-account-emissions-updated-response-topic");

        JsonDeserializer<EmailNotification> emailDeserializer = new JsonDeserializer<>(EmailNotification.class);
        emailConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("email-group"), new StringDeserializer(), emailDeserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(emailConsumer, "group.notification.topic");
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

        account = new Account();
        account.setIdentifier(111111L);
        account.setFullIdentifier("fullIdentifier");
        account.setAccountName("TEST_ACCOUNT");
        account.setStatus(Status.ACTIVE);
        account.setOpeningDate(new Date());
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCommitmentPeriodCode(1);
        account.setAccountType("INSTALLATION");
        account.setCheckDigits(2);

        accountRepository.saveAndFlush(account);

        installation = new Installation();
        installation.setIdentifier(123456L);
        installation.setStartYear(2021);
        installation.setActivityType("test");
        installation.setPermitIdentifier("123");
        installation.setAccount(account);

        installationRepository.saveAndFlush(installation);
        account.setCompliantEntity(installation);
        accountRepository.saveAndFlush(account);
    }

    @AfterEach
    protected void cleanUp() {
        dltConsumer.commitSync();
        responseConsumer.commitSync();
        emailConsumer.commitSync();
        account.setCompliantEntity(null);
        accountRepository.saveAndFlush(account);
        installationRepository.delete(installation);
        accountRepository.delete(account);
        repository.deleteAll();
    }

    @AfterAll
    protected void tearDown() {
        dltConsumer.close();
        responseConsumer.close();
        emailConsumer.close();
        super.tearDown();
    }

    @Test
    void testEmissionEvent() throws JsonProcessingException {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2023));

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountEmissionsUpdateEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_EMISSIONS_UPDATED_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);

        // then
        ConsumerRecords<String, AccountEmissionsUpdateEventOutcome>
            records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountEmissionsUpdateEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountEmissionsUpdateEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        ComplianceOutbox complianceOutbox = repository.findAll()
            .stream()
            .filter(outbox -> outbox.getCompliantEntityId() == 123456L)
            .findFirst()
            .get();
        assertThat(complianceOutbox.getStatus()).isEqualTo(ComplianceOutboxStatus.PENDING);
        assertThat(complianceOutbox.getCompliantEntityId()).isEqualTo(123456);

        UpdateOfVerifiedEmissionsEvent emissionsEvent =
            objectMapper.readValue(complianceOutbox.getPayload(), UpdateOfVerifiedEmissionsEvent.class);
        assertThat(emissionsEvent.getActorId()).isEqualTo("system");
        assertThat(emissionsEvent.getCompliantEntityId()).isEqualTo(123456);
        assertThat(emissionsEvent.getType()).isEqualTo(ComplianceEventType.UPDATE_OF_VERIFIED_EMISSIONS);
        assertThat(emissionsEvent.getYear()).isEqualTo(2023);
        assertThat(emissionsEvent.getVerifiedEmissions()).isEqualTo(123L);
    }

    @Test
    void testEmissionEventWithBusinessError() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2020));

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountEmissionsUpdateEvent> providedEvent =
            new ProducerRecord<>(ACCOUNT_EMISSIONS_UPDATED_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);

        // then
        ConsumerRecords<String, AccountEmissionsUpdateEventOutcome>
            records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountEmissionsUpdateEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountEmissionsUpdateEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors()).isNotEmpty();

        List<ComplianceOutbox> outboxList = repository.findAll();
        assertThat(outboxList).isEmpty();

        ConsumerRecords<String, EmailNotification>
            emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 4);
        assertThat(emailRecords.count()).isEqualTo(4);
    }

    @Test
    void testEmissionEventInvalidMessages() {
        // given
        DefaultKafkaProducerFactory<String, String> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new StringSerializer());
        KafkaTemplate<String, String> stringKafkaTemplate = new KafkaTemplate<>(producerFactory);

        doThrow(new RuntimeException()).when(validator).validate(any(AccountEmissionsUpdateEvent.class));

        // when
        stringKafkaTemplate.send(ACCOUNT_EMISSIONS_UPDATED_TOPIC, "test");
        stringKafkaTemplate.send(ACCOUNT_EMISSIONS_UPDATED_TOPIC, "1111","{}");

        // then
        ConsumerRecords<String, String>
            records = KafkaTestUtils.getRecords(dltConsumer, Duration.ofMillis(10000), 2);
        assertThat(records.count()).isEqualTo(2);
        Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
        ConsumerRecord<String, String> invalidFormat = iterator.next();
        assertThat(invalidFormat.key()).isNull();
        assertThat(invalidFormat.value()).isEqualTo("test");
        ConsumerRecord<String, String> emptyMessage = iterator.next();
        assertThat(emptyMessage.key()).isEqualTo("1111");
        assertThat(emptyMessage.value()).isEqualTo("{\"registryId\":null,\"reportableEmissions\":null,\"reportingYear\":null}");
    }
}
