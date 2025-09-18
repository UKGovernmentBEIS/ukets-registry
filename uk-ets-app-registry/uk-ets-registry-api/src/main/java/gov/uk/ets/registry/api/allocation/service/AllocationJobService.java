package gov.uk.ets.registry.api.allocation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.domain.AllocationJobError;
import gov.uk.ets.registry.api.allocation.domain.QAllocationJob;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobErrorRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSearchCriteria;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.search.SearchUtils;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Repository for allocation job errors.
     */
    private final AllocationJobErrorRepository errorRepository;

    public AllocationJob getJob(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid allocation Job."));
    }

    public Page<AllocationJob> searchAllocationJobs(AllocationJobSearchCriteria criteria, Pageable pageable) {
        QAllocationJob allocationJob = QAllocationJob.allocationJob;
        BooleanExpression expression = allocationJob.isNotNull();
        expression = appendExpression(criteria.getId(), expression, allocationJob.id::eq);
        expression = appendExpression(criteria.getRequestIdentifier(), expression, allocationJob.requestIdentifier::eq);
        expression = appendExpression(criteria.getStatus(), expression, allocationJob.status::eq);

        expression = appendExpression(criteria.getExecutionDateFrom(), expression, t -> SearchUtils.getFromDatePredicate(t, allocationJob.updated));
        expression = appendExpression(criteria.getExecutionDateTo(), expression, t -> SearchUtils.getUntilDatePredicate(t, allocationJob.updated));

        return repository.findAll(expression, pageable);
    }

    private <T> BooleanExpression appendExpression(T param, BooleanExpression existing, Function<T, BooleanExpression> append) {
        return param == null ? existing : existing.and(append.apply(param));
    }

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
     * Sets the allocation job metadata.
     * @param allocationJob The allocation job.
     * @param errors The errors.
     */
    @Transactional
    public void setErrors(AllocationJob allocationJob, Map<Integer, String> errors) {
        Date now = new Date();

        List<AllocationJobError> errorList = errors.entrySet()
            .stream()
            .map(error -> {
                AllocationJobError allocationJobError = new AllocationJobError();
                allocationJobError.setAllocationJob(allocationJob);
                allocationJobError.setErrorCode(error.getKey());
                allocationJobError.setDetails(error.getValue());
                allocationJobError.setDateOccurred(now);
                return allocationJobError;
            }).toList();
        errorRepository.saveAll(errorList);

        allocationJob.getErrors().addAll(errorList);
        allocationJob.setUpdated(now);
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
    public void cancelPendingJob(Long allocationJobId) {
        AllocationJob scheduledJob = repository.findById(allocationJobId)
            .filter(job -> job.getStatus() == AllocationJobStatus.SCHEDULED)
            .orElseThrow(() -> new BusinessRuleErrorException(ErrorBody.from("Invalid Allocation job.")));

        scheduledJob.setStatus(AllocationJobStatus.CANCELLED);
        scheduledJob.setUpdated(new Date());
        repository.save(scheduledJob);
    }

}
