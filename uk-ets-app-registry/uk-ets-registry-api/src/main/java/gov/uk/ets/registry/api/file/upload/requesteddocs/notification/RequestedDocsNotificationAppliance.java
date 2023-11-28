package gov.uk.ets.registry.api.file.upload.requesteddocs.notification;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestDTO;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestType;
import gov.uk.ets.registry.api.notification.DocumentRequestGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.RequestDocumentUploadTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class RequestedDocsNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;
    private final NotificationService notificationService;

    private final UserService userService;
    private final TaskRepository taskRepository;
    private final Mapper mapper;

    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used:<br> {@link GroupNotificationType#DOCUMENT_REQUEST} or <br>
     *     {@link GroupNotificationType#DOCUMENT_REQUEST_FINALISATION}</li>
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
            if (GroupNotificationType.DOCUMENT_REQUEST.equals(groupNotificationType)) {
                groupNotificationClient.emitGroupNotification(
                        generateDocumentRequestNotification(groupNotificationType, joinPoint, (Long) result));

            } else if (GroupNotificationType.DOCUMENT_REQUEST_FINALISATION.equals(groupNotificationType)) {
                TaskCompleteResponse taskCompleteResponse = (TaskCompleteResponse) result;
                groupNotificationClient.emitGroupNotification(
                        generateDocumentRequestFinalisationNotification(groupNotificationType,
                                joinPoint,
                                taskCompleteResponse.getRequestIdentifier()));
            }
        }
    }


    private GroupNotification generateDocumentRequestNotification(GroupNotificationType groupNotificationType,
                                                                  JoinPoint joinPoint,
                                                                  Long requestId) {

        DocumentsRequestDTO documentsRequestDTO = (DocumentsRequestDTO) joinPoint.getArgs()[0];
        DocumentsRequestType documentsRequestType = documentsRequestDTO.getType();

        return DocumentsRequestType.ACCOUNT_HOLDER.equals(documentsRequestType)
                ? generateDocumentRequestNotificationForAccountHolder(groupNotificationType, documentsRequestDTO, requestId)
                : generateDocumentRequestNotificationForUser(groupNotificationType, documentsRequestDTO, requestId);
    }

    private GroupNotification generateDocumentRequestNotificationForAccountHolder(GroupNotificationType groupNotificationType,
                                                                                  DocumentsRequestDTO documentsRequestDTO,
                                                                                  Long requestId) {

        Pair<String, String> accountDetails = notificationService.findAccountNameAndAccountHolderNameFromDifference(requestId);

        return DocumentRequestGroupNotification.builder()
                .recipients(notificationService.findEmailOfArByUserUrid(documentsRequestDTO.getRecipientUrid(), false))
                .requestId(Objects.toString(requestId, null))
                .type(groupNotificationType)
                .documentsRequestType(documentsRequestDTO.getType())
                .accountName(accountDetails.getLeft())
                .accountHolderName(accountDetails.getRight())
                .build();
    }

    private GroupNotification generateDocumentRequestNotificationForUser(GroupNotificationType groupNotificationType,
                                                                         DocumentsRequestDTO documentsRequestDTO,
                                                                         Long requestId) {

        Task task = notificationService.findTaskByRequestId(requestId);

        return DocumentRequestGroupNotification.builder()
                .recipients(notificationService.findEmailOfArByUserUrid(documentsRequestDTO.getRecipientUrid(), false))
                .requestId(Objects.toString(requestId, null))
                .type(groupNotificationType)
                .documentsRequestType(documentsRequestDTO.getType())
                .userId(Utils.maskUserId(task.getUser().getUrid()))
                .userFullName(task.getUser().getFirstName() + " " + task.getUser().getLastName())
                .build();
    }

    private GroupNotification generateDocumentRequestFinalisationNotification(
            GroupNotificationType groupNotificationType,
            JoinPoint joinPoint,
            Long requestId) {

        RequestDocumentUploadTaskDetailsDTO requestDocumentUploadTaskDetailsDTO =
                (RequestDocumentUploadTaskDetailsDTO) joinPoint.getArgs()[0];
        DocumentsRequestType documentsRequestType = DocumentsRequestType.fromTaskRequestType(requestDocumentUploadTaskDetailsDTO.getTaskType());

        return DocumentsRequestType.ACCOUNT_HOLDER.equals(documentsRequestType)
                ? generateDocumentRequestFinalisationNotificationForAccountHolder(groupNotificationType, requestDocumentUploadTaskDetailsDTO, documentsRequestType, requestId)
                : generateDocumentRequestFinalisationNotificationForUser(groupNotificationType, requestDocumentUploadTaskDetailsDTO, documentsRequestType, requestId);
    }

    private GroupNotification generateDocumentRequestFinalisationNotificationForAccountHolder(
            GroupNotificationType groupNotificationType,
            RequestDocumentUploadTaskDetailsDTO requestDocumentUploadTaskDetailsDTO,
            DocumentsRequestType documentsRequestType,
            Long requestId) {

        Pair<String, String> accountDetails = notificationService.findAccountNameAndAccountHolderNameFromDifference(requestId);
        String accountName = accountDetails.getLeft();
        String accountHolderName = accountDetails.getRight();

        if (accountDetails.getLeft() == null ) {
            Task task = taskRepository.findByRequestId(requestId);
            if (task.getParentTask() != null ) {
                Task parentTask = taskRepository.findByRequestId(task.getParentTask().getRequestId());
                AccountDTO accountDTO = mapper.convertToPojo(parentTask.getDifference(), AccountDTO.class);
                if (accountDTO.getAccountDetails().getName() != null) {
                    accountName = accountDTO.getAccountDetails().getName();
                } else {
                    accountName = task.getAccount().getAccountName();
                }
            }
        }
        return DocumentRequestGroupNotification.builder()
                .recipients(getDocumentRequestFinalisationRecipientEmail(requestDocumentUploadTaskDetailsDTO))
                .requestId(Objects.toString(requestId, null))
                .type(groupNotificationType)
                .documentsRequestType(documentsRequestType)
                .accountName(accountName)
                .accountHolderName(accountHolderName)
                .build();
    }

    private GroupNotification generateDocumentRequestFinalisationNotificationForUser(
            GroupNotificationType groupNotificationType,
            RequestDocumentUploadTaskDetailsDTO requestDocumentUploadTaskDetailsDTO,
            DocumentsRequestType documentsRequestType,
            Long requestId) {

        User user = this.userService.getUserByUrid(requestDocumentUploadTaskDetailsDTO.getUserUrid());
        return DocumentRequestGroupNotification.builder()
                .recipients(getDocumentRequestFinalisationRecipientEmail(requestDocumentUploadTaskDetailsDTO))
                .requestId(Objects.toString(requestId, null))
                .type(groupNotificationType)
                .documentsRequestType(documentsRequestType)
                .userId(Utils.maskUserId(requestDocumentUploadTaskDetailsDTO.getUserUrid()))
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    private Set<String> getDocumentRequestFinalisationRecipientEmail(RequestDocumentUploadTaskDetailsDTO requestDocumentUploadTaskDetailsDTO) {
        Optional<User> parentTaskClaimant = Optional.of(requestDocumentUploadTaskDetailsDTO)
            .map(TaskDetailsDTO::getParentTask)
            .map(TaskDetailsDTO::getRequestId)
            .map(taskRepository::findByRequestId)
            .map(Task::getClaimedBy);

        if (parentTaskClaimant.isPresent()) {
            return notificationService.findEmailOfArByUserUrid(parentTaskClaimant.get().getUrid(), false);
        }
        return notificationService.findEmailOfArByUserUrid(requestDocumentUploadTaskDetailsDTO.getInitiatorUrid(), false);
    }
}
