package gov.uk.ets.registry.api.allocation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import java.util.Date;
import java.util.List;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Allocation Scheduler Job execution")
@ExtendWith(MockitoExtension.class)
class AllocationSchedulerTest {

    private AllocationScheduler scheduler;

    @Mock
    private AllocationCalculationService calculationService;
    @Mock
    private AllocationJobService allocationJobService;
    @Mock
    private AllocationSchedulerService schedulerService;
    @Mock
    private BusinessCheckExecutionService checkService;
    @Mock
    private CheckPendingAllocationJob pendingAllocationJob;
    @Mock
    private CheckPendingAllocationTransaction pendingAllocationTransaction;
    @Mock
    private CheckAllocationValid allocationValid;
    @Mock
    private CheckAllocationAccountHasEnoughUnits allocationAccountHasEnoughUnits;
    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {
        scheduler = new AllocationScheduler(
            calculationService,
            allocationJobService,
            schedulerService,
            checkService,
            pendingAllocationJob,
            pendingAllocationTransaction,
            allocationValid,
            allocationAccountHasEnoughUnits,
            eventService);
        scheduler.setMaxAllocationJobDelay(100L);
        scheduler.setRetryAllocationJobDelay(35L);
    }

    @Test
    @DisplayName("Execute one job without failures")
    void testExecuteAllocationJob() {
        // given
        LockAssert.TestHelper.makeAllAssertsPass(true);

        AllocationJob allocationJob = new AllocationJob();
        allocationJob.setCreated(Date.from(Instant.now()));
        allocationJob.setYear(2022);
        allocationJob.setCategory(AllocationCategory.INSTALLATION);

        BusinessCheckResult result = new BusinessCheckResult();
        AllocationOverview overview = new AllocationOverview();

        given(allocationJobService.getScheduledJobs()).willReturn(List.of(allocationJob));

        given(calculationService.calculateAllocationsOverview(2022, AllocationCategory.INSTALLATION)).willReturn(overview);
        given(checkService.execute(any(BusinessCheckContext.class), eq(List.of(pendingAllocationJob,
            pendingAllocationTransaction, allocationValid, allocationAccountHasEnoughUnits)))).willReturn(result);

        // when
        scheduler.execute();

        // then
        verify(allocationJobService, times(1)).setStatus(allocationJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, allocationJob);
    }

    @Test
    @DisplayName("Execute three jobs with retries and without failures")
    void testExecuteAllocationJobsWithRetries() {
        // given
        LockAssert.TestHelper.makeAllAssertsPass(true);

        AllocationJob firstJob = new AllocationJob();
        firstJob.setRequestIdentifier(1L);
        firstJob.setCreated(Date.from(Instant.now().minusSeconds(10)));
        firstJob.setYear(2021);
        firstJob.setCategory(AllocationCategory.INSTALLATION);
        AllocationJob secondJob = new AllocationJob();
        secondJob.setRequestIdentifier(2L);
        secondJob.setCreated(Date.from(Instant.now()));
        secondJob.setYear(2022);
        secondJob.setCategory(AllocationCategory.INSTALLATION);
        AllocationJob thirdJob = new AllocationJob();
        thirdJob.setRequestIdentifier(3L);
        thirdJob.setCreated(Date.from(Instant.now().plusSeconds(10)));
        thirdJob.setYear(2022);
        thirdJob.setCategory(AllocationCategory.AIRCRAFT_OPERATOR);

        BusinessCheckResult resultWithoutErrors = new BusinessCheckResult();

        BusinessCheckResult resultWithErrors = new BusinessCheckResult();
        resultWithErrors.setErrors(List.of(new BusinessCheckError()));

        AllocationOverview overview = new AllocationOverview();

        given(allocationJobService.getScheduledJobs()).willReturn(List.of(firstJob, secondJob, thirdJob));
        given(checkService.execute(any(BusinessCheckContext.class), eq(List.of(pendingAllocationTransaction))))
            .willReturn(resultWithErrors)     // secondJob first attempt 0ms
            .willReturn(resultWithErrors)     // secondJob second attempt 35ms
            .willReturn(resultWithErrors)     // secondJob second attempt 70ms
            .willReturn(resultWithoutErrors); // rest calls

        given(calculationService.calculateAllocationsOverview(2021, AllocationCategory.INSTALLATION)).willReturn(overview);
        given(calculationService.calculateAllocationsOverview(2022, AllocationCategory.INSTALLATION)).willReturn(overview);
        given(calculationService.calculateAllocationsOverview(2022, AllocationCategory.AIRCRAFT_OPERATOR)).willReturn(overview);
        given(checkService.execute(any(BusinessCheckContext.class), eq(List.of(pendingAllocationJob,
            pendingAllocationTransaction, allocationValid, allocationAccountHasEnoughUnits)))).willReturn(resultWithoutErrors);

        // when
        scheduler.execute();

        // then
        verify(allocationJobService, times(1)).setStatus(firstJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, firstJob);
        verify(allocationJobService, times(1)).setStatus(secondJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, secondJob);
        verify(allocationJobService, times(1)).setStatus(thirdJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, thirdJob);
    }

    @Test
    @DisplayName("Execute three jobs with retries and second job fails")
    void testExecuteAllocationJobsWithRetriesAndFailures() {
        // given
        LockAssert.TestHelper.makeAllAssertsPass(true);

        AllocationJob firstJob = new AllocationJob();
        firstJob.setRequestIdentifier(1L);
        firstJob.setCreated(Date.from(Instant.now().minusSeconds(10)));
        firstJob.setYear(2021);
        firstJob.setCategory(AllocationCategory.INSTALLATION);
        AllocationJob secondJob = new AllocationJob();
        secondJob.setRequestIdentifier(2L);
        secondJob.setCreated(Date.from(Instant.now()));
        secondJob.setYear(2022);
        secondJob.setCategory(AllocationCategory.INSTALLATION);
        AllocationJob thirdJob = new AllocationJob();
        thirdJob.setRequestIdentifier(3L);
        thirdJob.setCreated(Date.from(Instant.now().plusSeconds(10)));
        thirdJob.setYear(2022);
        thirdJob.setCategory(AllocationCategory.AIRCRAFT_OPERATOR);

        BusinessCheckResult resultWithoutErrors = new BusinessCheckResult();

        BusinessCheckResult resultWithErrors = new BusinessCheckResult();
        resultWithErrors.setErrors(List.of(new BusinessCheckError()));

        AllocationOverview overview = new AllocationOverview();

        given(allocationJobService.getScheduledJobs()).willReturn(List.of(firstJob, secondJob, thirdJob));
        given(checkService.execute(any(BusinessCheckContext.class), eq(List.of(pendingAllocationTransaction))))
            .willReturn(resultWithErrors)     // secondJob first attempt 0ms
            .willReturn(resultWithErrors)     // secondJob second attempt 35ms
            .willReturn(resultWithErrors)     // secondJob second attempt 70ms
            .willReturn(resultWithErrors)     // thirdJob first attempt 0ms
            .willReturn(resultWithErrors)     // thirdJob second attempt 35ms
            .willReturn(resultWithoutErrors); // rest calls

        given(calculationService.calculateAllocationsOverview(2021, AllocationCategory.INSTALLATION)).willReturn(overview);
        given(calculationService.calculateAllocationsOverview(2022, AllocationCategory.INSTALLATION)).willReturn(overview);
        given(calculationService.calculateAllocationsOverview(2022, AllocationCategory.AIRCRAFT_OPERATOR)).willReturn(overview);
        given(checkService.execute(any(BusinessCheckContext.class), eq(List.of(pendingAllocationJob,
            pendingAllocationTransaction, allocationValid, allocationAccountHasEnoughUnits))))
            .willReturn(resultWithoutErrors)  // first job
            .willReturn(resultWithErrors)     // second job
            .willReturn(resultWithoutErrors); // third job

        // when
        scheduler.execute();

        // then
        verify(allocationJobService, times(1)).setStatus(firstJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, firstJob);
        verify(allocationJobService, times(1)).setStatus(secondJob, AllocationJobStatus.FAILED);
        verify(eventService, times(1)).createAndPublishEvent("2",
            null,
            "Allocation Job not started due to business errors. See logs for more details.",
            EventType.UPLOAD_ALLOCATION_TABLE,
            "Error in Allocation Job execution");
        verify(allocationJobService, times(1)).setStatus(thirdJob, AllocationJobStatus.RUNNING);
        verify(schedulerService, times(1)).launchAllocations(overview, thirdJob);
    }

}
