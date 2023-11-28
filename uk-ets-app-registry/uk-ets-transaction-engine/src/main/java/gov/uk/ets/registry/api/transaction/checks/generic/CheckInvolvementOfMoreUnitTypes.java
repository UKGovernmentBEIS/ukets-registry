package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.stereotype.Service;

/**
 * A transaction must not issue more than one unit type.
 */
@Service("check3009")
public class CheckInvolvementOfMoreUnitTypes extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();
        if (UnitType.MULTIPLE.equals(transaction.calculateUnitTypes())) {
            if (transaction.getType().isIssuance()) {
                addError(context, "A transaction must not issue more than one unit type");
            } else {
                addError(context, "A transaction must not involve more than one unit type");
            }
        }
    }
}
