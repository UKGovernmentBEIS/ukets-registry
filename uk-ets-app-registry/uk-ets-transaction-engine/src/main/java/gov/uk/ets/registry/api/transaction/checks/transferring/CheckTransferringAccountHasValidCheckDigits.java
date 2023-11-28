package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import org.springframework.stereotype.Service;

/**
 * The transferring account number is invalid with respect to its check digits.
 */
@Service("check1001")
public class CheckTransferringAccountHasValidCheckDigits extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (transferringAccount != null && hasInvalidCheckDigits(transferringAccount)) {
            addError(context, "The transferring account number is invalid with respect to its check digits");
        }
    }
}
