package gov.uk.ets.registry.api.user.forgot.passwd.service;

import javax.validation.constraints.NotNull;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.ResetPasswordSuccessNotification;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordResponse;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This aspect processing method is responsible for applying business logic if
 * the underlying conditions are met.
 * <ul>
 * <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 * <li>One of the following {@link GroupNotificationType} were used:
 * {@link GroupNotificationType#REQUEST_RESET_PASSWORD_LINK}</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class ResetPasswordSuccessNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;

    @AfterReturning(value = "@annotation(emitsGroupNotificationsAnnotation)", returning = "result")
    public void apply(JoinPoint joinPoint, @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType == GroupNotificationType.RESET_PASSWORD_SUCCESS) {
                ResetPasswordResponse response = (ResetPasswordResponse) result;
                if(response.isSuccess()) {
                    groupNotificationClient.emitGroupNotification(
                            ResetPasswordSuccessNotification.builder().emailAddress(response.getEmail()).build());                    
                }
            }
        }
    }
}
