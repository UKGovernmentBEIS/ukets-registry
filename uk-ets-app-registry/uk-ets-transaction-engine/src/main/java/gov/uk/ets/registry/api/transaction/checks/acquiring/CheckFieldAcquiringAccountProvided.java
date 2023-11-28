package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import org.springframework.stereotype.Service;

/**
 * Enter account number.
 */
@Service("check2501")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckFieldAcquiringAccountProvided extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        String fullIdentifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        if (FullAccountIdentifierParser.getInstance(fullIdentifier).isEmpty()) {
            addError(context, "Enter account number");
        }
    }
}
