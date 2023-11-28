package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import org.springframework.stereotype.Service;


/**
 * The transferring and acquiring accounts must be the same.
 */
@Service("check2013")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringTransferringAccountEquality extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        AccountSummary transferringAccount = getTransferringAccount(context);
        AccountSummary acquiringAccount = getAcquiringAccount(context);

        if (!transferringAccount.getFullIdentifier().equals(acquiringAccount.getFullIdentifier())) {
            addError(context, "The transferring and acquiring accounts must be the same");
        }
    }
}

