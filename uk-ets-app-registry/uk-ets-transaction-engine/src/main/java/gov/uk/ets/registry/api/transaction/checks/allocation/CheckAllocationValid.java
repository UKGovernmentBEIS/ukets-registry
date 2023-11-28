package gov.uk.ets.registry.api.transaction.checks.allocation;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import org.springframework.stereotype.Service;

/**
 * Business check: An allocation job cannot start if there are no allowances to be allocated for the specified year
 * (i.e. all records in the allocation plan have already been fully allocated or flagged as withheld).
 */
@Service("check8004")
public class CheckAllocationValid extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AllocationOverview allocationOverview = context.get(AllocationOverview.class.getName(), AllocationOverview.class);

        if (allocationOverview.getTotalQuantity() <= 0) {
            addError(context, "An allocation job cannot start if there are no allowances to be allocated for the specified year (i.e. all records in the allocation plan have already been fully allocated or flagged as withheld)");
        }

    }

}
