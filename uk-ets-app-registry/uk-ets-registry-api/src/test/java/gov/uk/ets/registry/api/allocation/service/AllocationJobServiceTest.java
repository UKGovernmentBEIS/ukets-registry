package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

}