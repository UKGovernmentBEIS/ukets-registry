package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.springframework.stereotype.Service;

/**
 * Transactions to acquiring accounts with status CLOSURE PENDING or CLOSED are not allowed.
 */
@Service("check2003")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountHasNotClosedStatus extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        if (acquiringAccount != null &&
            AccountStatus.isClosedOrHasClosureRequests(acquiringAccount.getAccountStatus())) {
            if (context.hasPermission()) {
                addError(context, "Transactions to acquiring accounts with status " +
                                  "CLOSURE PENDING or CLOSED are not allowed");
            } else {
                addError(context, "The account number you entered could not be found");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5906;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Account has been closed (2003)";
    }
}
