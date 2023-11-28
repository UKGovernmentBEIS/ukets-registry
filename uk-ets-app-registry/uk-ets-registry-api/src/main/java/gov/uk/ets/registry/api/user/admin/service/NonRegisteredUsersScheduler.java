package gov.uk.ets.registry.api.user.admin.service;

import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NonRegisteredUsersScheduler {

    
    /**
     * Service for performing users cleanup.
     */
    private NonRegisteredUsersService nonRegisteredUsersService;

    /**
     * Initiates an non registered keycloak users cleanup at a scheduled time.
     */
    @SchedulerLock(name = "nonRegisteredUsersSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.non.registered.users.cleanup:0 0 23 * * *}")
    public void execute() {
        LockAssert.assertLocked();
        nonRegisteredUsersService.cleanUp();
    }
    
    
}
