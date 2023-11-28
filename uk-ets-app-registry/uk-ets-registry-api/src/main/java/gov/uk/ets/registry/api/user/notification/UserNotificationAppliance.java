package gov.uk.ets.registry.api.user.notification;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.notification.EmailChangeUserStatusNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.UserDeactivationNotification;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateNotification;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.UserDeactivationTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: {@link GroupNotificationType#EMAIL_CHANGE_STATUS}</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
@Log4j2
public class UserNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;
    private final NotificationService notificationService;
    private final UserService userService;
    private final UserAdministrationService userAdministrationService;

    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used: {@link GroupNotificationType#EMAIL_CHANGE_STATUS}
     *     {@link GroupNotificationType#USER_DETAILS_UPDATE_REQUEST}
     *     {@link GroupNotificationType#USER_DETAILS_UPDATE_COMPLETED}
     *     {@link GroupNotificationType#USER_DEACTIVATION_COMPLETED}</li>
     * </ul>
     *
     * @param joinPoint                         the joint point
     * @param emitsGroupNotificationsAnnotation the annotation
     * @param result                            the result of the intercepted method
     */
    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {

        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (GroupNotificationType.EMAIL_CHANGE_STATUS == groupNotificationType && result != null) {
                UserStatusChangeResultDTO userStatusChangeResultDTO = (UserStatusChangeResultDTO) result;

                if (UserStatus.SUSPENDED.equals(userStatusChangeResultDTO.getPreviousUserStatus()) ||
                    UserStatus.SUSPENDED.equals(userStatusChangeResultDTO.getUserStatus())) {
                    return;
                } else {
                    groupNotificationClient.emitGroupNotification(
                        generateArValidatedWhenAddedOrReplacedOnAccount((UserStatusChangeResultDTO) result));
                }
            } else if (GroupNotificationType.USER_DETAILS_UPDATE_REQUEST == groupNotificationType && result != null) {
                groupNotificationClient.emitGroupNotification(
                    generateUserDetailsUpdateNotification(joinPoint, (Long) result));
            } else if (GroupNotificationType.USER_DETAILS_UPDATE_COMPLETED == groupNotificationType && result != null) {
                groupNotificationClient.emitGroupNotification(
                    generateUserDetailsUpdateCompleteNotification(joinPoint, (TaskCompleteResponse) result));
            } else if (GroupNotificationType.USER_DEACTIVATION_REQUEST.equals(groupNotificationType) &&
                result != null) {
                log.info("Emails of type {} has been disabled", GroupNotificationType.USER_DEACTIVATION_REQUEST);
            } else if (isApprovedDeactivation(groupNotificationType, joinPoint) && result != null) {
                log.info("Emails of type {} has been disabled", GroupNotificationType.USER_DEACTIVATION_COMPLETED);

            }
        }
    }

    @Deprecated
    /**
     * @deprecated Keeping it in case we revisit this use case, and it turns out we need to send emails to the RAs
     * (not the end-users)
     */
    private GroupNotification generateUserDeactivationRequestNotification(JoinPoint joinPoint, Long requestId) {
        User user = userService.getUserByUrid((String) joinPoint.getArgs()[0]);
        UserRepresentation userRepresentation = userAdministrationService.findByIamId(user.getIamIdentifier());
        return UserDeactivationNotification.builder()
            .emailAddress(userRepresentation.getEmail())
            .requestId(Objects.toString(requestId, null))
            .userId(user.getUrid())
            .type(GroupNotificationType.USER_DEACTIVATION_REQUEST)
            .build();
    }

    private GroupNotification generateUserDetailsUpdateNotification(
        JoinPoint joinPoint, Long requestId) {
        UserDetailsUpdateDTO userDetailsUpdateDTO = (UserDetailsUpdateDTO) joinPoint.getArgs()[1];
        String userId = userDetailsUpdateDTO.getCurrent().getUrid();
        return UserDetailsUpdateNotification.builder()
            .emailAddress(userDetailsUpdateDTO.getCurrent().getEmailAddress())
            .requestId(Objects.toString(requestId, null))
            .userId(Utils.maskUserId(userId))
            .type(GroupNotificationType.USER_DETAILS_UPDATE_REQUEST)
            .build();
    }

    private GroupNotification generateUserDetailsUpdateCompleteNotification(JoinPoint joinPoint,
                                                                            TaskCompleteResponse result) {
        UserDetailsUpdateTaskDetailsDTO userDetailsUpdateTaskDetailsDTO =
            (UserDetailsUpdateTaskDetailsDTO) joinPoint.getArgs()[0];
        TaskOutcome taskOutcome = (TaskOutcome) joinPoint.getArgs()[1];
        String userId = userDetailsUpdateTaskDetailsDTO.getCurrent().getUrid();
        return UserDetailsUpdateNotification.builder()
            .emailAddress(userDetailsUpdateTaskDetailsDTO.getCurrent().getEmailAddress())
            .requestId(Objects.toString(result.getRequestIdentifier(), null))
            .userId(Utils.maskUserId(userId))
            .taskOutcome(taskOutcome)
            .type(GroupNotificationType.USER_DETAILS_UPDATE_COMPLETED)
            .build();
    }

    @Deprecated
    /**
     * @deprecated Keeping it in case we revisit this use case, and it turns out we need to send emails to the RAs
     * (not the end-users)
     */
    private GroupNotification generateUserDeactivationCompleteNotification(JoinPoint joinPoint,
                                                                           TaskCompleteResponse result) {
        UserDeactivationTaskDetailsDTO userDeactivationTaskDetailsDTO =
            (UserDeactivationTaskDetailsDTO) joinPoint.getArgs()[0];
        String userId = userDeactivationTaskDetailsDTO.getChanged().getEnrolmentKeyDetails().getUrid();

        return UserDeactivationNotification.builder()
            .emailAddress(userDeactivationTaskDetailsDTO.getChanged().getUserDetails().getEmail())
            .requestId(Objects.toString(result.getRequestIdentifier(), null))
            .userId(userId)
            .type(GroupNotificationType.USER_DEACTIVATION_COMPLETED)
            .build();
    }

    private boolean isApprovedDeactivation(GroupNotificationType groupNotificationType, JoinPoint joinPoint) {
        if (groupNotificationType == GroupNotificationType.USER_DEACTIVATION_COMPLETED) {
            TaskOutcome outcome = (TaskOutcome) joinPoint.getArgs()[1];
            return outcome == TaskOutcome.APPROVED;
        }
        return false;
    }

    private GroupNotification generateArValidatedWhenAddedOrReplacedOnAccount(UserStatusChangeResultDTO user) {
        return EmailChangeUserStatusNotification.builder()
            .emailAddress(notificationService.getEmailAddressWhenChangingUserStatus(user.getIamIdentifier()))
            .build();
    }

}
