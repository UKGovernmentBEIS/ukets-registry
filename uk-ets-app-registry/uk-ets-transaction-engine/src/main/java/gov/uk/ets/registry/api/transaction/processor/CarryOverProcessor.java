package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;

/**
 * Parent processor for carry-overs.
 */
public abstract class CarryOverProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        unitMarkingService.markApplicablePeriod(
            transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier()),
            transaction.getType().getPredefinedAccountCommitmentPeriod());
    }
}
