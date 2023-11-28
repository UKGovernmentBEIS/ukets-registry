package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.task.web.model.EmailChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeNotificationService.NotifyTaskFinalizationCommand;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: {@link GroupNotificationType#EMAIL_CHANGE_REQUESTED}, {@link GroupNotificationType#EMAIL_CHANGE_APPROVED}, {@link GroupNotificationType#EMAIL_CHANGE_REJECTED}</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class EmailChangeNotificationAppliance {
    private final EmailChangeNotificationService notificationService;

    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
        @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
        Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType == GroupNotificationType.EMAIL_CHANGE_REQUESTED) {
                notificationService.emitEmailChangeNotifications((EmailChange)result);
            } else if (groupNotificationType == GroupNotificationType.EMAIL_CHANGE_TASK_COMPLETED) {
                EmailChangeTaskDetailsDTO dto = (EmailChangeTaskDetailsDTO) joinPoint.getArgs()[0];
                notificationService.emitEmailChangeFinalizationNotification(NotifyTaskFinalizationCommand.
                    builder()
                    .oldEmail(dto.getUserCurrentEmail())
                    .newEmail(dto.getUserNewEmail())
                    .outcome((TaskOutcome) joinPoint.getArgs()[1])
                    .comment((String) joinPoint.getArgs()[2])
                    .build());
            }
        }
    }
}
