package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckExecutionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.checks.allocation.CheckAllocationAccountHasEnoughUnits;
import gov.uk.ets.registry.api.transaction.checks.allocation.CheckAllocationValid;
import gov.uk.ets.registry.api.transaction.checks.allocation.CheckPendingAllocationJob;
import gov.uk.ets.registry.api.transaction.checks.allocation.CheckPendingAllocationTransaction;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler for allocations.
 */
@Service
@Log4j2
@Setter
@RequiredArgsConstructor
public class AllocationScheduler {

    /**
     * Service for calculations.
     */
    private final AllocationCalculationService calculationService;

    /**
     * Service for allocation jobs.
     */
    private final AllocationJobService allocationJobService;

    /**
     * Service for allocation scheduler.
     */
    private final AllocationSchedulerService schedulerService;

    /**
     * Service for business checks.
     */
    private final BusinessCheckExecutionService checkService;

    /**
     * Check for pending allocation jobs.
     */
    private final CheckPendingAllocationJob pendingAllocationJob;

    /**
     * Check for pending allocation transactions.
     */
    private final CheckPendingAllocationTransaction pendingAllocationTransaction;

    /**
     * Check for valid allocation job.
     */
    private final CheckAllocationValid allocationValid;

    /**
     * Check for the allocation account holdings.
     */
    private final CheckAllocationAccountHasEnoughUnits allocationAccountHasEnoughUnits;

    /**
     * The event service.
     */
    private final EventService eventService;

    /**
     * The maximum delay between each job execution.
     */
    @Value("${scheduler.allocation.job.delay.max.ms}")
    private long maxAllocationJobDelay;

    /**
     * The delay between each retry.
     */
    @Value("${scheduler.allocation.job.delay.retry.ms}")
    private long retryAllocationJobDelay;

    /**
     * Launches allocations.
     * <ul>
     *     <li>Checks whether an allocation job is scheduled.</li>
     *     <li>Retrieves the allocation year from the scheduled allocation job.</li>
     *     <li>Calculates the current allocation overview and details.</li>
     *     <li>Executes business checks.</li>
     *     <li>Launch allocations.</li>
     * </ul>
     */
    @Transactional
    @SchedulerLock(name = "allocationSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.allocation.start}")
    public void execute() {
        LockAssert.assertLocked();

        Deque<AllocationJob> allocationJobs = allocationJobService.getScheduledJobs()
            .stream()
            .sorted(Comparator.comparing(AllocationJob::getCreated))
            .collect(Collectors.toCollection(LinkedList::new));

        if (allocationJobs.isEmpty()) {
            log.info("No scheduled allocation job found.");
            return;
        }

        while (!allocationJobs.isEmpty()) {
            launchAllocations(allocationJobs.pollFirst());
            if (!allocationJobs.isEmpty()) {
                waitForTransactionsToBeCompleted();
            }
        }
    }

    private void waitForTransactionsToBeCompleted() {
        Instant retryUntil = null;

        while (true) {
            boolean canStart = canStart(retryUntil);

            if (canStart) {
                return;
            } else if (retryUntil == null) {
                retryUntil = Instant.now().plusMillis(maxAllocationJobDelay);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(retryAllocationJobDelay);
            } catch (InterruptedException e) {
                log.error("Interrupted exception while waiting for the next allocation job", e);
                Thread.currentThread().interrupt();
                return;
            }
        }

    }

    private boolean canStart(Instant retryUntil) {
        boolean expiredDelay = Optional.ofNullable(retryUntil)
            .filter(until -> Instant.now().isAfter(until))
            .isPresent();
        if (expiredDelay) {
            return true;
        }
        BusinessCheckResult result = checkService.execute(new BusinessCheckContext(), List.of(pendingAllocationTransaction));
        return result.success();
    }

    private void launchAllocations(AllocationJob allocationJob) {
        Integer allocationYear = allocationJob.getYear();
        AllocationCategory allocationCategory = allocationJob.getCategory();
        log.info("Allocation job {} for year {} and category {} just woke up.",
            allocationJob.getId(), allocationYear, allocationCategory);

        AllocationOverview overview = calculationService.calculateAllocationsOverview(allocationYear, allocationCategory);

        BusinessCheckContext context = new BusinessCheckContext();
        context.store(AllocationOverview.class.getName(), overview);

        BusinessCheckResult result = checkService.execute(context, Arrays.asList(
            pendingAllocationJob, pendingAllocationTransaction, allocationValid, allocationAccountHasEnoughUnits));

        if (!result.success()) {
            Map<Integer, String> errors = result.getErrors().stream()
                .collect(Collectors.toMap(BusinessCheckError::getCode, BusinessCheckError::getMessage));
            log.error("Allocation job not started due to business errors. Errors: {}", errors.values());
            allocationJobService.setStatus(allocationJob, AllocationJobStatus.FAILED);
            allocationJobService.setErrors(allocationJob, errors);
            eventService.createAndPublishEvent(allocationJob.getRequestIdentifier().toString(),
                                               null,
                                               "Allocation Job not started due to business errors. See logs for more details.",
                                               EventType.UPLOAD_ALLOCATION_TABLE,
                                               "Error in Allocation Job execution");
            log.info("Allocation job status changed to FAILED.");
            return;
        }

        log.info("Allocation job {} for year {} and category {} went through successful business checking. " +
                "Allocation transactions are about to be executed.", allocationJob.getId(), allocationYear, allocationCategory);

        allocationJobService.setStatus(allocationJob, AllocationJobStatus.RUNNING);

        schedulerService.launchAllocations(overview, allocationJob);
    }

}
