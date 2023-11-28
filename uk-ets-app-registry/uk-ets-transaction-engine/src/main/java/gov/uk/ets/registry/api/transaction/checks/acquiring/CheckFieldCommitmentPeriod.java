package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * Checks the acquiring account commitment period.
 */
@Service("check2505")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckFieldCommitmentPeriod extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance(fullIdentifier);
        if (!parser.hasValidCommitmentPeriod()) {
            addError(context, "Invalid account number format - The period must be 1 digit long");
        }
        if (parser.belongsToRegistry() && !parser.hasCommitmentPeriod()) {
            addError(context, "Invalid account number format – The period must be specified for UK registry accounts");
        }
    }
}
