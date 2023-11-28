package gov.uk.ets.registry.api.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.CreateETSTransactionCommand;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionMessageRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@Log4j2
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@EmbeddedKafka(topics = {
        "registry.originating.notification.topic",
        "proposal.notification.in",
        "itl.notices.in.topic",
        "domain.event.topic",
        "group.notification.topic",
        "registry.originating.reconciliation.question.topic",
        "registry.originating.transaction.question.topic",
        "txlog.originating.reconciliation.answer.topic"
},
        brokerPropertiesLocation = "classpath:integration-test-application.properties",
        brokerProperties = "auto.create.topics.enable=false",
        count = 3,
        ports = {0,0,0}
)
@Import( {ReconciliationHelper.class, TransactionModelTestHelper.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReconciliationRaceConditionsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    TransactionMessageRepository transactionMessageRepository;

    @Autowired
    TransactionResponseRepository transactionResponseRepository;

    @Autowired
    TransactionBlockRepository transactionBlockRepository;

    @Autowired
    ReconciliationRepository reconciliationRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ProcessReconciliationService processReconciliationService;

    @Autowired
    TransactionModelTestHelper transactionModelTestHelper;

    @Autowired
    ReconciliationHelper helper;

    @Autowired
    ProcessReconciliationService reconciliationService;

    @Autowired
    ReconciliationHistoryRepository reconciliationHistoryRepository;

    @AfterEach
    @BeforeEach
    public void setup() {
        reconciliationHistoryRepository.deleteAll();
        reconciliationRepository.deleteAll();
        transactionBlockRepository.deleteAll();
        transactionHistoryRepository.deleteAll();
        transactionMessageRepository.deleteAll();
        transactionResponseRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void startedTransactionShouldBlockReconciliation() throws Exception {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        String identifier = "test";
        CountDownLatch latch = new CountDownLatch(2);
        // given a transaction that's waiting approval
        transactionModelTestHelper.addETSTransaction(CreateETSTransactionCommand.builder()
            .started(Date.from(LocalDateTime.now().minusHours(25).atZone(ZoneId.systemDefault()).toInstant()))
            .identifier(identifier)
            .status(TransactionStatus.AWAITING_APPROVAL)
            .executionDate(LocalDateTime.now().minusDays(1))
            .build());

        // given the two runnable objects represented the race condition that while an ETS transaction is being started then then a reconciliation is being started.
        Runnable startTransactionRunnable = () -> {
            try {
                helper.startTransactionFirst(identifier);
            } finally {
                latch.countDown();
            }
        };
        Runnable startReconciliationRunnable = () -> {
            try {
                helper.startReconciliationImmediatelyAfterTransactionStart();
            } finally {
                latch.countDown();
            }
        };
        // when the two threads of the above runnable objects executed
        executorService.execute(startTransactionRunnable);
        executorService.execute(startReconciliationRunnable);
        helper.awaitOnLatch(latch);
        executorService.shutdown();

        // then the transaction should be started by changing its status to PROPOSED
        assertEquals(TransactionStatus.PROPOSED, transactionRepository.findByIdentifier(identifier).getStatus());
        // then the ets transaction should block the reconciliation start.
        assertEquals(0, reconciliationRepository.findByStatus(ReconciliationStatus.INITIATED).size(),
            "the reconciliation should be blocked");
    }

    @Test
    void startedReconciliationShouldBlockTransaction() throws Exception {
        // given
        String identifier = "test";
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // given a transaction that's waiting approval
        transactionModelTestHelper.addETSTransaction(CreateETSTransactionCommand.builder()
            .started(Date.from(LocalDateTime.now().minusHours(25).atZone(ZoneId.systemDefault()).toInstant()))
            .identifier(identifier)
            .status(TransactionStatus.AWAITING_APPROVAL)
            .executionDate(LocalDateTime.now().minusDays(1))
            .build());
        // given the two runnable objects represented the race condition that while a reconciliation has been started an ETS transaction is being started.
        Runnable startReconciliationRunnable = () -> {
           try {
               helper.startReconciliationFirst();
           } finally {
               latch.countDown();
           }
        };
        Runnable startTransactionRunnable = () -> {
            try {
                helper.startTransactionImmediatelyAfterReconciliationStart(identifier);
            } finally {
                latch.countDown();
            }
        };

        // when the two threads of the above runnable objects executed
        executorService.execute(startTransactionRunnable);
        executorService.execute(startReconciliationRunnable);
        helper.awaitOnLatch(latch);
        executorService.shutdown();

        // then the reconciliation should be initiated
        assertEquals(1, reconciliationRepository.findByStatus(ReconciliationStatus.INITIATED).size(),
            "the reconciliation should be blocked");
        // then the reconciliation should block the transaction.
        assertEquals(TransactionStatus.AWAITING_APPROVAL,
            transactionRepository.findByIdentifier(identifier).getStatus());
    }

    @Test
    void transactionShouldNotBlockOtherTransactions() {
        // given 3 ets transactions that will be executed concurrently
        List<String> transactionIds = List.of("UK0001", "UK0002", "UK0003");
        CountDownLatch transactionsConcurrentCommitLatch = new CountDownLatch(transactionIds.size());
        CountDownLatch latch = new CountDownLatch(transactionIds.size());
        transactionIds.forEach(txId -> {
            try {
                transactionModelTestHelper.addETSTransaction(CreateETSTransactionCommand.builder()
                    .started(Date.from(LocalDateTime.now().minusHours(25).atZone(ZoneId.systemDefault()).toInstant()))
                    .identifier(txId)
                    .status(TransactionStatus.AWAITING_APPROVAL)
                    .executionDate(LocalDateTime.now().minusDays(1))
                    .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        List<Runnable> runnables = transactionIds.stream().map(txId ->
            (Runnable) () -> {
                helper.startTransaction(txId, transactionsConcurrentCommitLatch);
                latch.countDown();
            }
        ).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(transactionIds.size());

        // when the transactions start concurrently
        runnables.forEach(executorService::execute);
        executorService.shutdown();

        helper.awaitOnLatch(latch);

        // then all the transactions should be started
        transactionIds.forEach(txId -> assertEquals(TransactionStatus.PROPOSED, transactionRepository.findByIdentifier(txId).getStatus()));
    }
}
