package gov.uk.ets.registry.api.transaction.checks.allocation;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Business check: An allocation job cannot start if there are allocation transactions pending execution.
 */
@Service("check8002")
@AllArgsConstructor
public class CheckPendingAllocationTransaction extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        if (transactionRepository.countByTypeAndStatusNotIn(TransactionType.AllocateAllowances,
            TransactionStatus.getFinalStatuses()) > 0) {
            addError(context, "An allocation job cannot start if there are allocation transactions pending execution");
        }
    }

}
