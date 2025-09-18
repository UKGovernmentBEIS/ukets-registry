package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.profile.domain.PasswordChangeSuccessNotification;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import jakarta.validation.constraints.NotNull;
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
 *     used: {@link GroupNotificationType#PASSWORD_CHANGE_SUCCESS},
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class PasswordChangeNotificationAppliance {
    private final GroupNotificationClient groupNotificationClient;
    private final UserService userService;
    private final UserAdministrationService userAdministrationService;

    /**
     * Handles notifications concerning password  change.
     */
    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType == GroupNotificationType.PASSWORD_CHANGE_SUCCESS) {
                groupNotificationClient.emitGroupNotification(generatePasswordChangeNotification());
            }
        }
    }

    private GroupNotification generatePasswordChangeNotification() {
        String imaIdentifier = userService.getCurrentUser().getIamIdentifier();
        String email = userAdministrationService.findByIamId(imaIdentifier).getEmail();
        return PasswordChangeSuccessNotification.builder()
            .email(email)
            .build();
    }

}
