package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Transactions to accounts with other pending transactions of the same type are not allowed.
 */
@Service("check3013")
public class CheckPendingTransaction extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionType type = context.getTransactionType();

        if (transactionRepository.countByTypeAndStatusNotIn(type, TransactionStatus.getFinalStatuses()) > 0) {
            addError(context, "Transactions to accounts with other pending transactions of the same type are not allowed");
        }
    }
}
