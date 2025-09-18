package gov.uk.ets.registry.api.notification.scheduler;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.RequestDeadlineNotification;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This scheduler will periodically check if a task has reached the middle or two days before the deadline
 * and notify the claimant via email.
 */
@Log4j2
@Component
@AllArgsConstructor
public class DeadlineReminderScheduler {

    private TaskRepository taskRepository;
    private UserService userService;
    private GroupNotificationClient groupNotificationClient;

    @Transactional
    @SchedulerLock(name = "deadlineReminderSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.deadline.reminder.start}")
    public void sendReminders() {

        log.info("Request deadline reminder scheduler has started");

        List<String> applicableTypes =
            List.of(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD.name(), RequestType.AH_REQUESTED_DOCUMENT_UPLOAD.name());
        List<Task> toBeReminded = taskRepository.findMidWayOrTwoDaysBeforeDeadline(LocalDate.now(), applicableTypes);

        toBeReminded.stream()
            .filter(task -> !userService.isAdminUser(task.getClaimedBy()))
            .map(task -> RequestDeadlineNotification.builder()
                .recipients(Set.of(task.getClaimedBy().getEmail()))
                .requestId(Long.toString(task.getRequestId()))
                .deadline(task.getDeadline())
                .type(GroupNotificationType.DEADLINE_REMINDER)
                .build())
            .forEach(groupNotificationClient::emitGroupNotification);
    }
}
