package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;

/**
 * Defines the contract of a transaction processor.
 */
public interface TransactionProcessor {


    /**
     * Creates the initial transaction.
     *
     * @param transaction The transaction details.
     * @return a transaction
     */
    Transaction createInitialTransaction(TransactionSummary transaction);

    /**
     * Proposes a transaction.
     *
     * @param transaction The transaction details.
     */
    void propose(TransactionSummary transaction);

    /**
     * Performs finalisation actions for the provided transaction.
     *
     * @param transaction The transaction.
     */
    void finalise(Transaction transaction);

    /**
     * Performs the necessary actions to reject a transaction.
     *
     * @param transaction The transaction.
     */
    void reject(Transaction transaction);

    /**
     * Performs the necessary actions to terminate a transaction.
     *
     * @param transaction The transaction.
     */
    void terminate(Transaction transaction);

    /**
     * Performs the necessary actions to cancel a transaction.
     *
     * @param transaction The transaction.
     */
    void cancel(Transaction transaction);

    /**
     * Performs the necessary actions to manually cancel a transaction.
     *
     * @param transaction The transaction.
     */
    void manuallyCancel(Transaction transaction);

    /**
     * Performs the necessary actions for a failed transaction.
     *
     * @param transaction The transaction.
     */
    void fail(Transaction transaction);

    /**
     * Performs the necessary actions for a delayed transaction.
     *
     * @param transaction The transaction.
     */
    void delay(Transaction transaction);

}
