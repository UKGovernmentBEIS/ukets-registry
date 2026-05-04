package gov.uk.ets.registry.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountClaimSchedulerServiceTest {

    @Mock
    private TaskScheduler taskScheduler;

    @InjectMocks
    private AccountClaimSchedulerService schedulerService;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Captor
    private ArgumentCaptor<Instant> instantCaptor;

    @Test
    void scheduleBulkClaim_shouldScheduleTask() {
        // when
        schedulerService.scheduleBulkClaim();

        // then
        verify(taskScheduler, atLeastOnce())
                .schedule(runnableCaptor.capture(), instantCaptor.capture());

        Runnable scheduledTask = runnableCaptor.getValue();
        Instant scheduledDate = instantCaptor.getValue();

        assertNotNull(scheduledTask);
        assertNotNull(scheduledDate);
    }
}
