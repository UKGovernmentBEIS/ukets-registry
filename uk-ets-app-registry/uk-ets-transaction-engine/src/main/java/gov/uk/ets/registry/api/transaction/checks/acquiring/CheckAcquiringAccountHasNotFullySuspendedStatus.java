package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.springframework.stereotype.Service;

/**
 * Transactions to acquiring accounts with status FULLY SUSPENDED are not allowed.
 */
@Service("check2004")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountHasNotFullySuspendedStatus extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        if (acquiringAccount != null && AccountStatus.SUSPENDED.equals(acquiringAccount.getAccountStatus())) {
            if (context.hasPermission()) {
                addError(context, "Transactions to acquiring accounts with status FULLY SUSPENDED are not allowed");
            } else {
                addError(context, "You cannot currently transfer to this account");
            }
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
        return "Acquiring account is not eligible to receive units (2004)";
    }
}
