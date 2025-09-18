package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledNotificationsRetriever {

    private final NotificationSchedulingRepository schedulingRepository;

    public List<Notification> getNotificationsToBeSent(List<NotificationType> notificationTypes) {
        return schedulingRepository.getActiveNotifications(notificationTypes)
            .stream()
            .filter(notification -> !notificationAlreadySentToday(notification))
            .filter(this::notificationMustBeSentToday)
            .filter(this::startTimeHasPassed)
            .collect(toList());
    }

    /**
     * Checks if last execution date (day) is today.
     *
     * @return true if the notification has already been sent today, false otherwise.
     */
    private boolean notificationAlreadySentToday(Notification notification) {
        LocalDateTime lastExecutionDate = notification.getLastExecutionDate();
        if (lastExecutionDate == null) {
            return false;
        }
        return lastExecutionDate.toLocalDate().isEqual(LocalDate.now(UTC));
    }

    private boolean notificationMustBeSentToday(Notification notification) {
        LocalDateTime lastExecutionDate = notification.getLastExecutionDate();
        // first time this notification is sent:
        if (lastExecutionDate == null) {
            return true;
        }
        Integer runEveryXDays = notification.getSchedule().getRunEveryXDays();
        // the notification has no recurrence:
        if (runEveryXDays == null) {
            return false;
        }
        return checkRecurrence(lastExecutionDate.toLocalDate(), LocalDate.now(UTC), runEveryXDays);
    }

    /**
     * Recursively checks if the notification must be executed today, compared to the last execution date.
     * The method goes back from today subtracting runEveryXDays in every step.
     * If it reaches exactly the last execution date it means that the notification must be executed today.
     * If it reaches before last execution date it means that the notification must not be executed today.
     */
    private boolean checkRecurrence(LocalDate lastExecDate, LocalDate comparingDate, Integer runEveryXDays) {
        if (comparingDate.equals(lastExecDate)) {
            return true;
        } else if (comparingDate.isBefore(lastExecDate)) {
            return false;
        } else {
            return checkRecurrence(lastExecDate, comparingDate.minusDays(runEveryXDays), runEveryXDays);
        }
    }

    /**
     * We must also check the start time to decide if it is time to send the notification.
     */
    private boolean startTimeHasPassed(Notification notification) {
        LocalTime startTime = notification.getSchedule().getStartDateTime().toLocalTime();
        return startTime.isBefore(LocalTime.now(UTC)) || startTime.equals(LocalTime.now(UTC));
    }
}
