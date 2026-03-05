package gov.uk.ets.registry.api.integration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.integration.changelog.domain.IntegrationChangeLog;
import gov.uk.ets.registry.api.integration.changelog.repository.IntegrationChangeLogRepository;
import gov.uk.ets.registry.api.integration.service.withhold.WithholdEventValidator;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;

import java.time.Duration;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@TestPropertySource(locations = "/integration-test-application.properties", properties = {
        "kafka.integration.enabled=true",
        "kafka.authentication.enabled=false",
        "kafka.integration.installation.withhold.enabled=true"
})
@Disabled
public class WithholdEventIntegrationTest extends BaseIntegrationTest {

    private static final String ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC = "installation-registry-withhold-request-topic";
    private static final String ACCOUNT_WITHHOLD_UPDATED_RESPONSE_TOPIC = "registry-installation-withhold-response-topic";

    private KafkaTemplate<String, AccountWithholdUpdateEvent> kafkaTemplate;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InstallationRepository installationRepository;
    @Autowired
    private IntegrationChangeLogRepository changeLogRepository;
    @Autowired
    private AllocationYearRepository allocationYearRepository;
    @Autowired
    private AllocationEntryRepository allocationEntryRepository;
    @Autowired
    private AllocationStatusRepository allocationStatusRepository;

    @Autowired
    private Configuration freemarkerConfiguration;

    @SpyBean
    private WithholdEventValidator validator;

    private Consumer<String, String> dltConsumer;
    private Consumer<String, AccountWithholdUpdateEventOutcome> responseConsumer;
    private Consumer<String, EmailNotification> emailConsumer;

    private Installation installation;
    private Account account;

    @BeforeAll
    public void init() throws InterruptedException {
        // Clear duplicate assignments
        List<Account> accounts = accountRepository.findAll();
        accounts.forEach(a -> a.setCompliantEntity(null));
        accountRepository.saveAllAndFlush(accounts);

        // Setup kafka template
        DefaultKafkaProducerFactory<String, AccountWithholdUpdateEvent> producerFactory =
                new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                        new StringSerializer(),
                        new JsonSerializer<>());
        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        JsonDeserializer<AccountWithholdUpdateEventOutcome> deserializer = new JsonDeserializer<>(AccountWithholdUpdateEventOutcome.class);
        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("response-group"), new StringDeserializer(), deserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, ACCOUNT_WITHHOLD_UPDATED_RESPONSE_TOPIC);

        dltConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("dlt-group"), new StringDeserializer(),
                new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dltConsumer, ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC + "-dlt");

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
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
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
        account.setAccountType("ETS - Aircraft operator holding account");
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setCheckDigits(2);

        accountRepository.saveAndFlush(account);

        installation = new Installation();
        installation.setIdentifier(123456L);
        installation.setStartYear(2021);
        installation.setPermitIdentifier("123");
        installation.setAccount(account);

        installationRepository.saveAndFlush(installation);

        AllocationYear allocationYear = allocationYearRepository.findByYear(2022);

        AllocationEntry entry = new AllocationEntry();
        entry.setEntitlement(100L);
        entry.setCompliantEntityId(installation.getId());
        entry.setType(AllocationType.NAVAT);
        entry.setAllocationYear(allocationYear);
        allocationEntryRepository.saveAndFlush(entry);

        AllocationStatus status = new AllocationStatus();
        status.setStatus(AllocationStatusType.ALLOWED);
        status.setAllocationYear(allocationYear);
        status.setCompliantEntityId(installation.getId());
        allocationStatusRepository.saveAndFlush(status);

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
        allocationEntryRepository.deleteAll();
        allocationStatusRepository.deleteAll();
        installationRepository.delete(installation);
        accountRepository.delete(account);
        changeLogRepository.deleteAll();
    }

    @Test
    void testWithholdEvent() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123456L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2022));

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountWithholdUpdateEvent> providedEvent =
                new ProducerRecord<>(ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);

        // then
        ConsumerRecords<String, AccountWithholdUpdateEventOutcome>
                records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);

        ConsumerRecord<String, AccountWithholdUpdateEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountWithholdUpdateEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();

        List<IntegrationChangeLog> changeLogs = changeLogRepository.findAll();
        assertThat(changeLogs.size()).isEqualTo(1);
        IntegrationChangeLog changeLog = changeLogs.get(0);
        assertThat(changeLog.getFieldChanged()).isEqualTo("withhold");
        assertThat(changeLog.getOldValue()).isEqualTo("Year: 2022, Withhold_Flag: ALLOWED");
        assertThat(changeLog.getNewValue()).isEqualTo("Year: 2022, Withhold_Flag: WITHHELD");
        assertThat(changeLog.getEntity()).isEqualTo("AllocationStatus");
        assertThat(changeLog.getAccountNumber()).isEqualTo("fullIdentifier");
        assertThat(changeLog.getOperatorId()).isEqualTo(123456);
        assertThat(changeLog.getUpdatedBy()).isEqualTo("METS-Installation");
        assertThat(changeLog.getUpdatedAt()).isNotNull();
    }

    @Test
    void testWithholdEventWithBusinessError() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123456L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2020));

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, AccountWithholdUpdateEvent> providedEvent =
                new ProducerRecord<>(ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC, null, null, "123456", event, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);

        // then
        ConsumerRecords<String, AccountWithholdUpdateEventOutcome>
                records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        ConsumerRecord<String, AccountWithholdUpdateEventOutcome> record = records.iterator().next();
        assertThat(record.headers().lastHeader("Correlation-Id").value()).isEqualTo("correlationId".getBytes());
        AccountWithholdUpdateEventOutcome response = record.value();
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors()).hasSize(1);

        ConsumerRecords<String, EmailNotification>
                emailRecords = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 4);
        assertThat(emailRecords.count()).isEqualTo(1);
    }

    @Test
    void testEmissionEventInvalidMessages() {
        // given
        DefaultKafkaProducerFactory<String, String> producerFactory =
                new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                        new StringSerializer(),
                        new StringSerializer());
        KafkaTemplate<String, String> stringKafkaTemplate = new KafkaTemplate<>(producerFactory);

        doThrow(new RuntimeException()).when(validator).validate(any(AccountWithholdUpdateEvent.class));

        // when
        stringKafkaTemplate.send(ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC, "test");
        stringKafkaTemplate.send(ACCOUNT_WITHHOLD_UPDATED_REQUEST_TOPIC, "1111","{}");

        // then
        ConsumerRecords<String, String> records =
                KafkaTestUtils.getRecords(dltConsumer, Duration.ofMillis(10000), 2);

        assertThat(records.count()).isEqualTo(2);
    }
}
