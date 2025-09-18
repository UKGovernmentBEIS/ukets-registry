package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.TokenChangeNotification;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect for token change notification e-mail messages.
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class TokenChangeNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;

    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (GroupNotificationType.TOKEN_CHANGE_REQUEST.equals(groupNotificationType)) {
                groupNotificationClient.emitGroupNotification((TokenChangeNotification)result);
            }
        }
    }
}
