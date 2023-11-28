package gov.uk.ets.registry.api.reconciliation.service;

import gov.uk.ets.registry.api.transaction.lock.RegistryLockProvider;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Checker which checks if pending UK transactions exist.
 */
@Component
@AllArgsConstructor
public class PendingUKTransactionsChecker {
    private ETSTransactionService etsTransactionService;

    private RegistryLockProvider registryLockProvider;
    /**
     * Checks if pending UK transactions exist.
     * @throws PendingUKTransactionsException Runtime exception if pending transactions exist.
     */
    public void check() {
        long pendingUKTransactions = etsTransactionService.countPendingETSTransactions();
        if (pendingUKTransactions > 0) {
            throw new PendingUKTransactionsException();
        }
        try {
            registryLockProvider.acquirePessimisticWriteLock(RegistryLockType.RECONCILIATION, true);
        } catch(javax.persistence.LockTimeoutException exception) {
            throw new PendingUKTransactionsException();
        }
    }

    /**
     * Runtime exception thrown when pending internal transactions exist during reconciliation initiation.
     */
    public static class PendingUKTransactionsException extends IllegalStateException {
        public PendingUKTransactionsException() {
            super("Reconciliation process aborted due to pending transactions existence.");
        }
    }
}
