package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for allocation jobs.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AllocationJobService {

    /**
     * Repository for allocation jobs.
     */
    private final AllocationJobRepository repository;

    /**
     * Retrieves the currently scheduled allocation jobs.
     * @return a list of allocation jobs.
     */
    public List<AllocationJob> getScheduledJobs() {
        return repository.findByStatus(AllocationJobStatus.SCHEDULED);
    }

    /**
     * Sets the allocation job status.
     * @param allocationJob The allocation job.
     * @param status The new status.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setStatus(AllocationJob allocationJob, AllocationJobStatus status) {
        allocationJob.setStatus(status);
        allocationJob.setUpdated(new Date());
        repository.save(allocationJob);
    }

    /**
     * Creates a new allocation job with status {@link AllocationJobStatus#SCHEDULED}.
     */
    @Transactional
    public void scheduleJob(Long requestId, Integer year, AllocationCategory category) {
        AllocationJob allocationJob = new AllocationJob();
        allocationJob.setStatus(AllocationJobStatus.SCHEDULED);
        allocationJob.setCreated(new Date());
        allocationJob.setRequestIdentifier(requestId);
        allocationJob.setYear(year);
        allocationJob.setCategory(category);
        repository.save(allocationJob);
    }

    /**
     * Cancels the current pending jobs.
     */
    @Transactional
    public void cancelPendingJobs() {
        var isAllocationJobRunning = repository.findByStatus(AllocationJobStatus.RUNNING);
        if (!isAllocationJobRunning.isEmpty()) {
            throw new BusinessRuleErrorException(ErrorBody.from("A cancellation request cannot be submitted while the allocation job is being executed."));
        }
        var scheduledJobs = getScheduledJobs();
        if (scheduledJobs.isEmpty()) {
            throw new BusinessRuleErrorException(ErrorBody.from("A cancellation request cannot be submitted if there is no pending allocation job."));
        }

        for (AllocationJob scheduledJob : scheduledJobs) {
            scheduledJob.setStatus(AllocationJobStatus.CANCELLED);
            scheduledJob.setUpdated(new Date());
            repository.save(scheduledJob);
        }
    }

}
