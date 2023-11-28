package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ExternalTransfer")
public class ExternalTransferProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        if (Constants.isInboundTransaction(transaction)) {
            unitCreationService.generateTransactionBlocks(transaction);
            updateStatus(transaction.getIdentifier(), TransactionStatus.ACCEPTED);        
            UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.
                getCurrentAccountBalances(transaction.getIdentifier(),
                        transaction.getLastUpdated(),
                        (String)null,
                        transaction.getAcquiringAccountFullIdentifier());
            transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
        } else {
            super.propose(transaction);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        if (Constants.isInboundTransaction(transaction)) {
            List<TransactionBlock> blocks = getTransactionBlocks(transaction.getIdentifier());
            unitCreationService.createUnitBlocks(transaction.getAcquiringAccount().getAccountIdentifier(), blocks);

        } else {
            unitTransferService.transferUnitsOutsideRegistry(transaction.getIdentifier());
        }
        super.finalise(transaction);
    }
}
