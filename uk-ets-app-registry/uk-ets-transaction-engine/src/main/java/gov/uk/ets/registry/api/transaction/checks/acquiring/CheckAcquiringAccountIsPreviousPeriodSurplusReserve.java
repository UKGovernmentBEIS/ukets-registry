package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import org.springframework.stereotype.Service;

/**
 * The acquiring account must be a PPSR account when the transferring account is also a PPSR account.
 */
@Service("check2012")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountIsPreviousPeriodSurplusReserve extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (transferringAccount != null &&
            AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.equals(transferringAccount.getType()) &&
            !AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.equals(acquiringAccount.getType())) {
            addError(context, "The acquiring account must be a PPSR account when the transferring account is also a PPSR account");
        }
    }
}
