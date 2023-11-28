package gov.uk.ets.registry.api.transaction.checks.allocation;

import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * An allocation job cannot start if there are other jobs pending execution.
 */
@Service("check8001")
@AllArgsConstructor
public class CheckPendingAllocationJob extends ParentBusinessCheck {

    /**
     * Repository for allocation jobs.
     */
    private AllocationJobRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        if (!repository.findByStatus(AllocationJobStatus.RUNNING).isEmpty()) {
            addError(context, "An allocation job cannot start if there are other jobs pending execution.");
        }
    }
}
