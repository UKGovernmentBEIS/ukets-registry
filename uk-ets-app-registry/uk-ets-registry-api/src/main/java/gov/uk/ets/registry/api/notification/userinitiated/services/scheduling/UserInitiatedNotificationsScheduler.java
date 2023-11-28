package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserInitiatedNotificationsScheduler {

    private final SchedulingNotificationService service;

    @Transactional
    @SchedulerLock(name = "userInitiatedNotificationStatusLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${user.initiated.notifications.scheduler}")
    public void scheduleUserInitiatedNotifications() {
        LockAssert.assertLocked();
        log.info("notification scheduling started...");
        service.scheduleNotifications();
        log.info("notification scheduling ended...");

    }
}
