package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Transactions from accounts with other pending transactions of the same type are not allowed.
 */
@Service("check1006")
public class CheckTransferringAccountForPendingTransactionOfSameType extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionType type = context.getTransactionType();
        AccountSummary transferringAccount = getTransferringAccount(context);

        if (transactionRepository.countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(type, transferringAccount.getFullIdentifier(),
            TransactionStatus.getFinalStatuses()) > 0) {
            addError(context, "Transactions from accounts with other pending transactions of the same type are not allowed, " +
                    "with the exception of transfers");
        }
    }
}
