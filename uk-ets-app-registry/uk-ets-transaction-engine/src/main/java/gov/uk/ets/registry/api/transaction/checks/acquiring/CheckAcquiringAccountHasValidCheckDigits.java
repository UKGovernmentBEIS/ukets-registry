package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * The acquiring account number is invalid with respect to its check digits.
 */
@Service("check2001")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountHasValidCheckDigits extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance(fullIdentifier);
        if (!parser.hasCheckDigits()) {
            addError(context, "Invalid account number format - The check digits must be up to 2 digits long");

        } else if (parser.belongsToRegistry() && parser.isEmptyCheckDigits()) {
            addError(context, "Invalid account number format – The check digits must be specified for UK registry account");

        } else if (parser.belongsToRegistry() && !parser.hasValidCheckDigits()) {
            addError(context, "Enter a valid account number");
        }
    }
}
