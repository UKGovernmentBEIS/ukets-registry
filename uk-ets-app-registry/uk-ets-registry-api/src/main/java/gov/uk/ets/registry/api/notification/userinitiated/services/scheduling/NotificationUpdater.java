package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import static java.time.ZoneOffset.UTC;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import java.time.LocalDateTime;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
public class NotificationUpdater {

    /**
     * Updates times fired and last execution date for all notifications.
     * Also updates end date time and status for the corner case of non-recurrent notifications (otherwise the status
     * would have been updated in next run of the scheduler).
     */
    public void update(Notification n) {
        n.setTimesFired(ObjectUtils.defaultIfNull(n.getTimesFired(), 0L) + 1);
        n.setLastExecutionDate(LocalDateTime.now(UTC));
        if (isNonRecurrent(n)) {
            NotificationSchedule schedule = n.getSchedule();
            schedule.setEndDateTime(LocalDateTime.now(UTC));
            n.setStatus(NotificationStatus.EXPIRED);
        }
    }

    private boolean isNonRecurrent(Notification n) {
        return n.getSchedule().getEndDateTime() == null &&
            ObjectUtils.defaultIfNull(n.getSchedule().getRunEveryXDays(), 0) == 0;
    }
}
