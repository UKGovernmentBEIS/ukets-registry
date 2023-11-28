package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * Transaction is not allowed for the type of transferring account.
 */
@Service("check1007")
public class CheckTransferringAccountHasValidType extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        TransactionSummary transaction = context.getTransaction();

        final List<AccountType> transferringAccountTypes = transaction.getType().getTransferringAccountTypes();
        if (transferringAccount != null && transferringAccountTypes != null && !transferringAccountTypes.contains(transferringAccount.getType())) {
            addError(context, "Transaction is not allowed for the type of transferring account");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5904;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Transaction inconsistent with Party policy (1007)";
    }
}
