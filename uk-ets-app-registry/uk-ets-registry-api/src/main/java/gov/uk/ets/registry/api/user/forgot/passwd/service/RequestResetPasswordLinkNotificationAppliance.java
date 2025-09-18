package gov.uk.ets.registry.api.user.forgot.passwd.service;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.RequestResetPasswordLinkNotification;
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
public class RequestResetPasswordLinkNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;

    @AfterReturning(value = "@annotation(emitsGroupNotificationsAnnotation)", returning = "result")
    public void apply(JoinPoint joinPoint, @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType == GroupNotificationType.REQUEST_RESET_PASSWORD_LINK) {

                ForgotPasswordEmailDTO response = (ForgotPasswordEmailDTO) result;
                if (response.isSuccess()) {
                    RequestResetPasswordLinkNotification groupNotification =
                        RequestResetPasswordLinkNotification.builder()
                            .emailAddress(response.getEmail())
                            .expiration(response.getExpiration())
                            .resetPasswordUrl(response.getConfirmationUrl()).build();
                    groupNotificationClient.emitGroupNotification(groupNotification);
                }
            }
        }
    }
}
