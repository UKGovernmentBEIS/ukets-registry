package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import org.springframework.stereotype.Service;

/**
 * The transferring account must be a PPSR account when the acquiring account is also a PPSR account.
 */
@Service("check1009")
public class CheckTransferringAccountIsPreviousPeriodSurplusReserve extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (transferringAccount != null &&
            AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.equals(acquiringAccount.getType()) &&
            !AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.equals(transferringAccount.getType())) {
            addError(context, "The transferring account must be a PPSR account when the acquiring account is also a PPSR account");
        }
    }
}
