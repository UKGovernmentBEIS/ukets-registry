package gov.uk.ets.registry.api.payment.scheduler;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.PaymentReminderNotification;
import gov.uk.ets.registry.api.payment.shared.PaymentTaskReminder;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This scheduler will periodically check:
 * <ol>
 * <li>If the payment sub-task is not completed within the N days</li>
 * <li>If the payment sub-task is not completed within the 2*N days,</li>
 * <li>If the payment sub-task is not completed within 3N-2 days.</li>
 * </ol>
 * where N is the configurable parameter <code>scheduler.payment.reminder.days.factor</code>
 * and notify the claimant via email.If N <=0 then no reminders are send.
 */
@Log4j2
@Component
public class PaymentReminderScheduler {

    private TaskRepository taskRepository;
    private UserService userService;
    private GroupNotificationClient groupNotificationClient;
    private Integer reminderDaysFactor;

    public PaymentReminderScheduler(
            TaskRepository taskRepository,
            UserService userService,
            GroupNotificationClient groupNotificationClient,
            @Value("${scheduler.payment.reminder.days.factor:7}") Integer reminderDaysFactor) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.groupNotificationClient = groupNotificationClient;
        this.reminderDaysFactor = reminderDaysFactor;
    }
    
    @Transactional
    @SchedulerLock(name = "paymentReminderSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.payment.reminder.start}")
    public void sendReminders() {

        log.info("Request payment reminder scheduler has started");
        
        if (reminderDaysFactor <= 0) {
            log.info("Request payment reminder scheduler disabled.");
            return;
        }
        
        List<PaymentTaskReminder> toBeReminded = taskRepository.findToBeRemindedPaymentTasks(reminderDaysFactor, reminderDaysFactor*2, reminderDaysFactor*3-2);

        toBeReminded.stream()
            .filter(task -> !userService.isAdminUser(userService.getUserByUrid(task.getClaimantUrid())))
            .map(task -> PaymentReminderNotification.builder()
                .recipients(Set.of(task.getClaimantEmail()))
                .initiationDate(task.getInitiatedDate())
                .requestId(task.getReferenceNumber())
                .amount(task.getAmountRequested())
                .description(task.getDescription())
                .type(GroupNotificationType.PAYMENT_REMINDER)
                .build())
            .forEach(groupNotificationClient::emitGroupNotification);
        
        log.info("Request payment reminder scheduler has ended");
    }
    
}
