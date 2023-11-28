package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import org.springframework.stereotype.Service;

/**
 * The transferring account number does not exist in the registry.
 */
@Service("check1002")
public class CheckTransferringAccountExists extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (!context.skipTransferringAccountChecks() && transferringAccount == null) {
            addError(context, "The transferring account number does not exist in the registry");
        }
    }
}
