package gov.uk.ets.registry.api.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.persistence.ReconciliationTestHelper;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationHistory;
import gov.uk.ets.registry.api.reconciliation.messaging.UKTLOutboundAdapter;
import gov.uk.ets.registry.api.reconciliation.messaging.UKTLOutboundAdapterProperties;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import gov.uk.ets.registry.api.reconciliation.service.ReconciliationActionService;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationFailedEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckPendingReconciliation;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(topics = {
    "registry.originating.notification.topic",
    "proposal.notification.in",
    "itl.notices.in.topic",
    "domain.event.topic",
    "group.notification.topic",
    "registry.originating.reconciliation.question.topic",
    "registry.originating.transaction.question.topic",
    "txlog.originating.reconciliation.answer.topic",
    "itl.originating.reconciliation.in.topic",
    "itl.originating.reconciliation.out.topic"
},
    brokerPropertiesLocation = "classpath:integration-test-application.properties",
    brokerProperties = "auto.create.topics.enable=false",
    count = 3,
    ports = {0, 0, 0}
)
public class ReconciliationIntegrationTest extends BaseIntegrationTest {

    @SpyBean
    UKTLOutboundAdapterProperties uktlOutboundAdapterProperties;

    @SpyBean
    UKTLOutboundAdapter uktlOutboundAdapter;

    @SpyBean
    ProcessReconciliationService processReconciliationServiceSpy;

    @Autowired
    ProcessReconciliationService processReconciliationService;

    @Autowired
    ReconciliationRepository reconciliationRepository;

    @Autowired
    ReconciliationHistoryRepository historyRepository;

    @Autowired
    ReconciliationFailedEntryRepository failedEntryRepository;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @Autowired
    KafkaTemplate<String, ReconciliationSummary> kafkaTemplate;

    @Autowired
    EntityManager entityManager;

    @SpyBean
    UKTLInboundAdapter uktlInboundAdapterSpy;

    @Autowired
    ReconciliationActionService reconciliationActionService;

    @Autowired
    ReconciliationTestHelper helper;

    @Autowired
    CheckPendingReconciliation checkPendingReconciliation;

    Consumer<String, ReconciliationSummary> consumer;

    @BeforeAll
    void setup() {
    }

    @AfterAll
    protected void tearDown() {
        consumer.close();
        super.tearDown();
    }

    @BeforeEach
    void setupEach() {
        Map<String, Object> config =
            new HashMap<>(KafkaTestUtils.consumerProps("transaction_log_messages", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(
            config, new StringDeserializer(),
            new JsonDeserializer<>(ReconciliationSummary.class, false)).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "registry.originating.reconciliation.question.topic");
        helper.clearAll();
    }

    @AfterEach
    void closeConsumer() {
        consumer.close();
    }

    @Transactional
    @Test
    @DisplayName("It should store the new reconciliation and send the reconciliation summary to UKTL kafka topic")
    void initiate() throws InterruptedException {
        // when
        processReconciliationService.initiate(new Date());

        // then
        List<Reconciliation> storedReconciliations = reconciliationRepository.findAll();
        assertEquals(1, storedReconciliations.size());
        List<ReconciliationHistory> storedReconciliationHistories = historyRepository.findAll();
        assertEquals(1, storedReconciliationHistories.size());
        assertEquals(storedReconciliations.get(0).getIdentifier(),
            storedReconciliationHistories.get(0).getReconciliation().getIdentifier());
        ConsumerRecord<String, ReconciliationSummary> consumerRecord = KafkaTestUtils.getSingleRecord(consumer,
            "registry.originating.reconciliation.question.topic");

        assertNotNull(consumerRecord);
    }

    @Transactional
    @Test
    void subsequentIntitiate() {
        // when
        helper.doInNewTransaction(() -> {
            processReconciliationService.initiate(new Date());
        });
        helper.doInNewTransaction(() -> {
            processReconciliationService.initiate(new Date());
        });
        helper.doInNewTransaction(() -> {
            processReconciliationService.initiate(new Date());
        });
        helper.doInNewTransaction(() -> {
            processReconciliationService.initiate(new Date());
        });
        helper.doInNewTransaction(() -> {
            processReconciliationService.initiate(new Date());
        });

        //then
        List<Reconciliation> initiatedReconciliations =
            reconciliationRepository.findByStatus(ReconciliationStatus.INITIATED);
        assertEquals(1, initiatedReconciliations.size());
        assertEquals(4, reconciliationRepository.findByStatus(ReconciliationStatus.INCONSISTENT).size());
        assertEquals(0, reconciliationRepository.findByStatus(ReconciliationStatus.COMPLETED).size());
    }

    @Test
    @DisplayName("Given an asynchronous kafka error on resonciliation initiation, the transaction should rollback.")
    void failedOnInteractingWithUKTLDuringInitiation() throws InterruptedException {
        // given
        given(uktlOutboundAdapterProperties.getReconciliationQuestionTopic())
            .willReturn("non-existed-topic-to-reproduce-the-asynchronous-error");

        // when
        try {
            processReconciliationService.initiate(new Date());
        } catch (Exception e) {
        }

        // then
        assertEquals(0, reconciliationRepository.findAll().size());
        assertEquals(0, historyRepository.findAll().size());
    }

    @Transactional
    @DisplayName("It should handle the transaction log answer as expected")
    @Test
    @Disabled("failing because of read_commited in consumer, needs investigation")
    void completesAsExpected() throws InterruptedException {
        //given
        List<Long> expectedFailedAccounts = List.of(101L, 102L, 103L);
        helper.initData(ReconciliationTestHelper.TestCase.builder()
            .blockedAccounts(expectedFailedAccounts)
            .uktlShouldReturnFailures(true)
            .build());
        Long identifier = 1003L;
        helper.createInitiatedReconciliation(identifier);

        // when
        kafkaTemplate.executeInTransaction(operations ->
            {
                try {
                    return kafkaTemplate
                        .send("txlog.originating.reconciliation.answer.topic",
                            buildSummary(identifier, expectedFailedAccounts))
                        .get();
                } catch (Exception e) {
                    throw new RuntimeException("failed to send message", e);
                }
            }
        );

        //then
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);
        then(uktlInboundAdapterSpy).should(times(1)).handleAnswer(any());
        then(processReconciliationServiceSpy).should().completeReconciliation(any());
        assertEquals(expectedFailedAccounts.size(), failedEntryRepository.count());
    }

    private ReconciliationSummary buildSummary(Long identifier, List<Long> expectedFailedAccounts) {
        return ReconciliationSummary.builder()
            .identifier(identifier)
            .failedEntries(expectedFailedAccounts.stream().map(accountIdentifier -> {
                ReconciliationFailedEntrySummary failedEntrySummary = new ReconciliationFailedEntrySummary();
                failedEntrySummary.setUnitType(UnitType.CER);
                failedEntrySummary.setAccountIdentifier(accountIdentifier);
                failedEntrySummary.setTotalInRegistry(3000L);
                failedEntrySummary.setTotalInTransactionLog(4000L);
                return failedEntrySummary;
            }).collect(Collectors.toList()))
            .status(ReconciliationStatus.INCONSISTENT)
            .build();
    }
}
