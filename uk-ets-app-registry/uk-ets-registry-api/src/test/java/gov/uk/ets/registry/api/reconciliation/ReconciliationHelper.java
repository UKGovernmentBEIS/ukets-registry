package gov.uk.ets.registry.api.reconciliation;

import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class ReconciliationHelper {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ProcessReconciliationService reconciliationService;

    CountDownLatch startReconciliationLatch = new CountDownLatch(1);
    CountDownLatch startTransactionLatch = new CountDownLatch(1);
    CountDownLatch commitTransactionTxLatch = new CountDownLatch(1);
    CountDownLatch commitReconciliationTxLatch = new CountDownLatch(1);

    @Transactional
    public void startTransactionFirst(String identifier) {
        transactionService.startTransaction(transactionRepository.findByIdentifier(identifier));
        startReconciliationLatch.countDown();
        awaitOnLatch(commitTransactionTxLatch);
    }

    @Transactional
    public void startReconciliationImmediatelyAfterTransactionStart() {
        awaitOnLatch(startReconciliationLatch);
        try {
            reconciliationService.initiate(new Date());
        } finally {
            commitTransactionTxLatch.countDown();
        }
    }

    @Transactional
    public void startReconciliationFirst() {
        reconciliationService.initiate(new Date());
        startTransactionLatch.countDown();
        awaitOnLatch(commitReconciliationTxLatch);
    }

    @Transactional
    public void startTransactionImmediatelyAfterReconciliationStart(String identier) {
        awaitOnLatch(startTransactionLatch);
        try {
            transactionService.startTransaction(transactionRepository.findByIdentifier(identier));
        } finally {
            commitReconciliationTxLatch.countDown();
        }
    }

    @Transactional
    public void startTransaction(String identier, CountDownLatch commitTransactionTxLatch) {
        transactionService.startTransaction(transactionRepository.findByIdentifier(identier));
        commitTransactionTxLatch.countDown();
        awaitOnLatch(commitTransactionTxLatch);
    }

    public void awaitOnLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
