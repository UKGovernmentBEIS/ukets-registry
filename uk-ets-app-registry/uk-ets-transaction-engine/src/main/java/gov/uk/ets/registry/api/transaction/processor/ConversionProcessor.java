package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ConversionProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        unitReservationService.reserveUnits(transaction);
        List<TransactionBlockSummary> proposedBlocks = transaction.getBlocks();
        TransactionBlockSummary block = proposedBlocks.get(0);
        unitCreationService.createTransactionBlocks(transaction.getIdentifier(), block.getProjectNumber(), block.getProjectTrack());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccountIdentifier(),transaction.getAcquiringAccountFullIdentifier());
        transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
    }
}
