package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyOtpChangeRequestedNotification;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyOtpChangeTaskApprovedNotification;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyPasswordOtpChangeRequestedNotification;
import gov.uk.ets.registry.api.user.profile.web.EmergencyTaskCompleteResponse;
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
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: {@link GroupNotificationType#EMERGENCY_OTP_CHANGE_REQUESTED},
 *     {@link GroupNotificationType#EMERGENCY_OTP_CHANGE_COMPLETE},
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class EmergencyOtpChangeNotificationAppliance {
    private final GroupNotificationClient groupNotificationClient;

    /**
     * Handles notifications concerning emergency OTP change.
     */
    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            // null result means the email should not be sent (invalid user email)
            if (groupNotificationType == GroupNotificationType.EMERGENCY_OTP_CHANGE_REQUESTED && result != null) {
                groupNotificationClient.emitGroupNotification(generateRequestNotification((EmergencyChange) result));
            }
            if (isAcceptedEmergencyOtpTask(groupNotificationType, joinPoint) && result != null) {
                groupNotificationClient
                    .emitGroupNotification(generateTaskNotification((EmergencyTaskCompleteResponse) result));
            }
            if (groupNotificationType == GroupNotificationType.EMERGENCY_PASSWORD_OTP_CHANGE_REQUESTED &&
                result != null) {
                groupNotificationClient
                    .emitGroupNotification(generatePasswordOtpRequestNotification((EmergencyChange) result));
            }
        }
    }

    private EmergencyOtpChangeRequestedNotification generateRequestNotification(EmergencyChange emergencyChange) {
        return EmergencyOtpChangeRequestedNotification.builder()
            .email(emergencyChange.getEmail())
            .verificationUrl(emergencyChange.getVerificationUrl())
            .expiration(emergencyChange.getExpiration())
            .build();
    }

    private EmergencyPasswordOtpChangeRequestedNotification generatePasswordOtpRequestNotification(
        EmergencyChange emergencyChange) {
        return EmergencyPasswordOtpChangeRequestedNotification.builder()
            .email(emergencyChange.getEmail())
            .verificationUrl(emergencyChange.getVerificationUrl())
            .expiration(emergencyChange.getExpiration())
            .build();
    }

    private boolean isAcceptedEmergencyOtpTask(GroupNotificationType groupNotificationType, JoinPoint joinPoint) {
        if (groupNotificationType == GroupNotificationType.EMERGENCY_OTP_CHANGE_COMPLETE ) {
            TaskOutcome outcome = (TaskOutcome) joinPoint.getArgs()[1];
            return outcome == TaskOutcome.APPROVED;
        }
        return false;
    }

    private EmergencyOtpChangeTaskApprovedNotification generateTaskNotification(
        EmergencyTaskCompleteResponse response) {
        return EmergencyOtpChangeTaskApprovedNotification.builder()
            .email(response.getEmail())
            .verificationUrl(response.getLoginUrl())
            .build();
    }

}
