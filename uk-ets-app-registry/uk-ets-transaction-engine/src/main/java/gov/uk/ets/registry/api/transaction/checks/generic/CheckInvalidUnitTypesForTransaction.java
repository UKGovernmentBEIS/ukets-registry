package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * Invalid unit types for this transaction.
 */
@Service("check3006")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckInvalidUnitTypesForTransaction extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();
        List<UnitType> allowedUnitTypes = transaction.getType().getUnits();
        List<TransactionBlockSummary> blocks = context.getBlocks();

        if (!CollectionUtils.isEmpty(allowedUnitTypes) &&
            blocks.stream().anyMatch(block -> !allowedUnitTypes.contains(block.getType()))) {
            addError(context, "Invalid unit types for this transaction");
        }
    }
}
