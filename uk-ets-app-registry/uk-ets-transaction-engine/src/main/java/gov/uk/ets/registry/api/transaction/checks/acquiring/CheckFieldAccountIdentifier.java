package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * Checks the acquiring account identifier.
 */
@Service("check2504")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckFieldAccountIdentifier extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance(fullIdentifier);
        if (!parser.hasValidIdentifier()) {
            addError(context, "Invalid account number format - The account ID must be numeric");

        } else if (!parser.hasValidIdentifierInRegistry()) {
            addError(context, "Invalid account number format - The account ID must be up to 8 digits long for UK Registry accounts");
        }
    }
}
