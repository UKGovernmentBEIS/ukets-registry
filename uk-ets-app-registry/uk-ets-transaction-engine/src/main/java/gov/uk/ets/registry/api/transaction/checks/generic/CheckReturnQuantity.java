package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The proposed quantity is more than the overallocated units.
 */
@Service("check3017")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
@RequiredArgsConstructor
public class CheckReturnQuantity extends ParentBusinessCheck {

    private final AllocationCalculationService allocationCalculationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();
        Long quantity = transaction.calculateQuantity();

        AccountSummary transferringAccount = getTransferringAccount(context);
        Optional<AllocationSummary> allocationEntry = allocationCalculationService.getAllocationEntry(
            transferringAccount.getIdentifier(), transaction.getAllocationType(), transaction.getAllocationYear());
        Long overallocationAmount = 0L;
        if (allocationEntry.isPresent()) {
            overallocationAmount = - allocationEntry.get().getRemaining();
        }

        if (quantity > overallocationAmount) {
            addError(context, "The proposed quantity is more than the overallocated units.");
        }
    }

}
