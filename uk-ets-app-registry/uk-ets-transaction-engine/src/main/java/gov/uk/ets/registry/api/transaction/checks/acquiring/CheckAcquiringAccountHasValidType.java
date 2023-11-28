package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * Transaction is not allowed for the type of acquiring account.
 */
@Service("check2007")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountHasValidType extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);

        final List<AccountType> acquiringAccountTypes = context.getTransactionType().getAcquiringAccountTypes();
        if (acquiringAccount != null && acquiringAccountTypes != null && !acquiringAccountTypes.contains(acquiringAccount.getType())) {
            addError(context, "Transaction is not allowed for the type of acquiring account");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5903;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Acquiring account is not eligible to receive units (2007)";
    }
}
