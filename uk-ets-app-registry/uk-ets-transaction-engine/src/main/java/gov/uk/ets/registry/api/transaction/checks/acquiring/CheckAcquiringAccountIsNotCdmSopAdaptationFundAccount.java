package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import org.springframework.stereotype.Service;

/**
 * External transfers cannot have as an acquiring account the CDM SOP Adaptation Fund Account.
 */
@Service("check2009")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountIsNotCdmSopAdaptationFundAccount extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        if (acquiringAccount.getFullIdentifier().equals(transactionAccountService.getSopAccountFullIdentifier())) {
            addError(context, "External transfers cannot have as an acquiring account the CDM SOP Adaptation Fund Account");
        }
    }
}
