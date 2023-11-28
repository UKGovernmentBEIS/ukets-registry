package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * Checks the acquiring account type.
 */
@Service("check2503")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckFieldAccountType extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        if (!FullAccountIdentifierParser.getInstance(fullIdentifier).hasValidType()) {
            addError(context, "Invalid account number format - The account type must be 3 digits long and a valid Kyoto type");
        }
    }
}
