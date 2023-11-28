package gov.uk.ets.registry.api.account.notification;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.accountclosure.web.model.AccountClosureTaskDetailsDTO;
import gov.uk.ets.registry.api.common.view.RequestDTO;
import gov.uk.ets.registry.api.notification.AccountClosureGroupNotification;
import gov.uk.ets.registry.api.notification.AccountManagementNotificationProperties;
import gov.uk.ets.registry.api.notification.AccountOpeningGroupNotification;
import gov.uk.ets.registry.api.notification.AccountProposalGroupNotification;
import gov.uk.ets.registry.api.notification.AccountUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.TrustedAccountUpdateDescriptionGroupNotification;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: {@link GroupNotificationType#ACCOUNT_UPDATE_PROPOSAL}</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class AccountNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;
    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used: {@link GroupNotificationType#ACCOUNT_UPDATE_PROPOSAL}</li>
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
            if (GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL.equals(groupNotificationType)) {
                GroupNotification groupNotification =
                    generateAccountUpdateProposalNotification(groupNotificationType, (Long) result);
                if (groupNotification != null) {
                    groupNotificationClient.emitGroupNotification(groupNotification);
                }
            } else if (isTaskRelatedToAccountManagement(groupNotificationType, joinPoint)) {
                groupNotificationClient.emitGroupNotification(
                    generateAccountUpdateOutcomeNotification(groupNotificationType, joinPoint
                    ));
            } else if (GroupNotificationType.ACCOUNT_OPENING_FINALISATION.equals(groupNotificationType) &&
                result instanceof TaskCompleteResponse) {
                groupNotificationClient.emitGroupNotification(
                    generateAccountOpeningNotification(groupNotificationType, (TaskCompleteResponse) result,
                        joinPoint));
            } else if (GroupNotificationType.TRUSTED_ACCOUNT_UPDATE_DESCRIPTION.equals(groupNotificationType) &&
                result instanceof TrustedAccountDTO) {
                groupNotificationClient.emitGroupNotification(
                    generateAccountUpdateProposalNotification(groupNotificationType,
                        (TrustedAccountDTO) result));
            } else if (GroupNotificationType.ACCOUNT_PROPOSAL.equals(groupNotificationType) &&
                result instanceof RequestDTO) {
                GroupNotification notification =
                    generateAccountProposalNotification(groupNotificationType, (RequestDTO) result, joinPoint);
                if (notification != null) {
                    groupNotificationClient.emitGroupNotification(notification);
                }
            }
        }
    }

    @Before("execution(* gov.uk.ets.registry.api.accountclosure.service.AccountClosureTaskService.complete(..))")
    public void apply(JoinPoint joinPoint) {

        AccountClosureTaskDetailsDTO taskDTO = (AccountClosureTaskDetailsDTO)joinPoint.getArgs()[0];
        TaskOutcome taskOutcome = (TaskOutcome)joinPoint.getArgs()[1];

        GroupNotification notification =
            generateAccountClosureNotification(GroupNotificationType.ACCOUNT_CLOSURE_COMPLETED, taskDTO, taskOutcome);
        if (notification != null) {
            groupNotificationClient.emitGroupNotification(notification);
        }

    }

    private GroupNotification generateAccountUpdateProposalNotification(
        GroupNotificationType notificationType, Long requestId) {
        // Added for UKETS- 4595: In case an SRA suspends an AR, no task is created
        if (requestId == null) {
            return null;
        }
        Long accountIdentifier = notificationService.findAccountIdentifierByRequestId(requestId);
        return AccountUpdateGroupNotification.builder()
            .recipients(notificationService.findEmailsOfArsByAccountIdentifier(accountIdentifier, false))
            .accountIdentifier(Objects.toString(accountIdentifier, null))
            .accountFullIdentifier(notificationService.findAccountFullIdentifierByRequestId(requestId))
            .requestType(notificationService.findRequestTypeById(requestId))
            .requestId(Objects.toString(requestId, null))
            .type(notificationType)
            .build();
    }

    private GroupNotification generateAccountUpdateProposalNotification(
        GroupNotificationType notificationType, TrustedAccountDTO trustedAccountDTO) {
        Long accountIdentifier = notificationService
            .findAccountIdentifierByIdAndAccountFullIdentifier(trustedAccountDTO.getId(),
                trustedAccountDTO.getAccountFullIdentifier());
        return TrustedAccountUpdateDescriptionGroupNotification.builder()
            .recipients(notificationService.findEmailsOfArsByAccountIdentifier(accountIdentifier, false))
            .accountIdentifier(Objects.toString(accountIdentifier, null))
            .accountFullIdentifier(trustedAccountDTO.getAccountFullIdentifier())
            .description(trustedAccountDTO.getDescription())
            .type(notificationType)
            .build();
    }

    private GroupNotification generateAccountClosureNotification(
        GroupNotificationType notificationType, AccountClosureTaskDetailsDTO taskDTO, TaskOutcome outcome) {

        Long requestId = taskDTO.getRequestId();
        Task task = notificationService.findTaskByRequestId(requestId);
        if (TaskOutcome.APPROVED.equals(outcome)) {
            Long accountIdentifier = TaskOutcome.APPROVED.equals(outcome) ? task.getAccount().getIdentifier() : null;
            String accountFullIdentifier =
                TaskOutcome.APPROVED.equals(outcome) ? task.getAccount().getFullIdentifier() : null;
            return AccountClosureGroupNotification
                .builder()
                .recipients(notificationService.findEmailsOfArsByAccountIdentifier(accountIdentifier, false))
                .accountFullIdentifier(accountFullIdentifier)
                .requestId(Objects.toString(requestId, null))
                .type(notificationType)
                .build();
        }
        return null;
    }

    private GroupNotification generateAccountUpdateOutcomeNotification(GroupNotificationType notificationType,
                                                                       JoinPoint joinPoint
    ) {
        Long requestId = (Long) joinPoint.getArgs()[0];
        TaskOutcome outcome = (TaskOutcome) joinPoint.getArgs()[1];
        Long accountIdentifier = notificationService.findAccountIdentifierByRequestId(requestId);
        return AccountUpdateGroupNotification.builder()
            .recipients(notificationService.findEmailsOfArsByAccountIdentifier(accountIdentifier, false))
            .accountIdentifier(Objects.toString(accountIdentifier, null))
            .accountFullIdentifier(notificationService.findAccountFullIdentifierByRequestId(requestId))
            .requestType(notificationService.findRequestTypeById(requestId))
            .requestId(Objects.toString(requestId, null))
            .type(notificationType)
            .taskOutcome(outcome)
            .build();
    }

    private GroupNotification generateAccountProposalNotification(
        GroupNotificationType notificationType, RequestDTO result, JoinPoint joinPoint) {
        Long requestId = result.getRequestId();
        AccountDTO accountDTO = (AccountDTO) joinPoint.getArgs()[0];
        Set<String> urids = accountDTO.getAuthorisedRepresentatives().stream().map(AuthorisedRepresentativeDTO::getUrid)
            .filter(urid -> !userService.getUser(urid).getStatus().equals(UserStatus.SUSPENDED))
            .filter(filterARUserStatuses(List.of(UserStatus.SUSPENDED, UserStatus.DEACTIVATION_PENDING,
                UserStatus.DEACTIVATED)))
            .collect(Collectors.toSet());

        if (urids.isEmpty()) {
            return null;
        }
        List<UserWorkContact> arsContacts = userService.getUserWorkContacts(urids);

        return AccountProposalGroupNotification
            .builder()
            .recipients(arsContacts.stream().map(UserWorkContact::getEmail).collect(Collectors.toSet()))
            .requestId(requestId.toString())
            .type(notificationType)
            .build();
    }

    private Predicate<String> filterARUserStatuses(List<UserStatus> userStatuses) {
        return Predicate.not(urid -> userStatuses.contains(userService.getUser(urid).getStatus()));
    }

    private GroupNotification generateAccountOpeningNotification(
        GroupNotificationType notificationType, TaskCompleteResponse result, JoinPoint joinPoint) {

        Long requestId = result.getRequestIdentifier();
        Task task = notificationService.findTaskByRequestId(requestId);
        TaskOutcome outcome = (TaskOutcome) joinPoint.getArgs()[1];
        Long accountIdentifier = TaskOutcome.APPROVED.equals(outcome) ? task.getAccount().getIdentifier() : null;
        String accountFullIdentifier =
            TaskOutcome.APPROVED.equals(outcome) ? task.getAccount().getFullIdentifier() : null;
        Long userId = task.getInitiatedBy().getId();
        String rejectionComment = (String) joinPoint.getArgs()[2];
        return AccountOpeningGroupNotification
            .builder()
            .recipients(notificationService.getEmailAddressesForAccountOpening(outcome, accountIdentifier, userId))
            .accountFullIdentifier(accountFullIdentifier)
            .requestId(Objects.toString(requestId, null))
            .type(notificationType)
            .taskOutcome(outcome)
            .rejectionComment(rejectionComment)
            .build();
    }

    /**
     * Filters out tasks unrelated to Account Management.
     **/
    private boolean isTaskRelatedToAccountManagement(GroupNotificationType groupNotificationType, JoinPoint joinPoint) {
        if (GroupNotificationType.TASK_COMPLETE_OUTCOME == groupNotificationType) {
            Long requestId = (Long) joinPoint.getArgs()[0];
            RequestType requestType = notificationService.findRequestTypeById(requestId);
            return AccountManagementNotificationProperties.ACCOUNT_MANAGEMENT_REQUEST_TYPES
                .containsKey(requestType);
        }
        return false;
    }
}
