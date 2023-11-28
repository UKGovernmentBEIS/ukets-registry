package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * Checks the acquiring account registry code.
 */
@Service("check2502")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckFieldRegistryCode extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        if (!FullAccountIdentifierParser.getInstance(fullIdentifier).hasValidRegistryCode()) {
            addError(context, "Invalid account number format - The country code must be 2 letters long and valid");
        }
    }

}
