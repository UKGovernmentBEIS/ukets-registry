package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.querydsl.core.types.dsl.BooleanExpression;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSearchCriteria;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class AllocationJobServiceTest {

    @InjectMocks
    AllocationJobService allocationJobService;

    @Mock
    AllocationJobRepository allocationJobRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        AllocationJob job = new AllocationJob();
        job.setStatus(AllocationJobStatus.SCHEDULED);
        job.setYear(2021);
        job.setCreated(new Date());
        job.setRequestIdentifier(new Random().nextLong());
        job.setCategory(AllocationCategory.INSTALLATION);
        Mockito.when(allocationJobRepository.findByStatus(any())).thenReturn(List.of(job));
    }

    @Test
    void testService() {
        List<AllocationJob> jobs = allocationJobService.getScheduledJobs();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());

        AllocationJob job = jobs.get(0);
        assertEquals(AllocationJobStatus.SCHEDULED, job.getStatus());

        allocationJobService.setStatus(job, AllocationJobStatus.COMPLETED);
        assertEquals(AllocationJobStatus.COMPLETED, job.getStatus());
        verify(allocationJobRepository, Mockito.times(1)).save(any());

        allocationJobService.scheduleJob(new Random().nextLong(), 2022, AllocationCategory.INSTALLATION);
        verify(allocationJobRepository, Mockito.times(2)).save(any());
    }

    @Test
    void testSearchAllocations() {
        // given
        AllocationJob job = new AllocationJob();
        job.setStatus(AllocationJobStatus.COMPLETED);
        Page<AllocationJob> page = new PageImpl<>(List.of(job));

        AllocationJobSearchCriteria criteria = new AllocationJobSearchCriteria();
        Pageable pageable = PageRequest.ofSize(10);

        Mockito.when(allocationJobRepository.findAll(any(BooleanExpression.class), eq(pageable))).thenReturn(page);

        // when
        Page<AllocationJob> result = allocationJobService.searchAllocationJobs(criteria, pageable);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals(job, result.getContent().get(0));
    }

    @Test
    void testCancelPendingJob() {
        // given
        AllocationJob job = new AllocationJob();
        job.setId(111L);
        job.setStatus(AllocationJobStatus.SCHEDULED);

        Mockito.when(allocationJobRepository.findById(111L)).thenReturn(Optional.of(job));

        // when
        allocationJobService.cancelPendingJob(111L);

        // then
        verify(allocationJobRepository, Mockito.times(1)).save(job);
        assertEquals(AllocationJobStatus.CANCELLED, job.getStatus());
    }

    @Test
    void testCancelPendingJobWithRunningJob() {
        // given
        AllocationJob job = new AllocationJob();
        job.setId(111L);
        job.setStatus(AllocationJobStatus.RUNNING);

        Mockito.when(allocationJobRepository.findById(111L)).thenReturn(Optional.of(job));

        // when
        BusinessRuleErrorException exception =
            assertThrows(BusinessRuleErrorException.class, () -> allocationJobService.cancelPendingJob(111L));

        // then
        assertEquals("Invalid Allocation job.", exception.getErrorBody().getErrorDetails().get(0).getMessage());
    }
}