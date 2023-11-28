package gov.uk.ets.registry.api.file.upload.requesteddocs.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferTaskDetailsDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestDTO;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestType;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.task.web.model.AccountOpeningTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestDocumentsWizardService {
    private final EventService eventService;
    private final PersistenceService persistenceService;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final AccountService accountService;
    private final UserRepository userRepository;
    private final AccountHolderService accountHolderService;
    private final TaskEventService taskEventService;
    private final Mapper mapper;

    /**
     * Creates a requested documents task.
     *
     * @param documentsRequest the documents request.
     * @return the Task ID.
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.DOCUMENT_REQUEST)
    public Long submitDocumentsRequest(DocumentsRequestDTO documentsRequest) {
        RequestDocumentsTaskDifference difference = new RequestDocumentsTaskDifference();
        Task task = new Task();
        task.setUser(userRepository.findByUrid(documentsRequest.getRecipientUrid()));
        difference.setDocumentNames(documentsRequest.getDocumentNames());
        Map<String, Long> uploadedFileNameIdMap = new HashMap<>();
        documentsRequest.getDocumentNames().forEach(d -> uploadedFileNameIdMap.put(d, null));
        difference.setUploadedFileNameIdMap(uploadedFileNameIdMap);
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setParentTask(taskRepository.findByRequestId(documentsRequest.getParentRequestId()));
        if (documentsRequest.getType().equals(DocumentsRequestType.ACCOUNT_HOLDER)) {
            difference.setAccountHolderIdentifier(documentsRequest.getAccountHolderIdentifier());
        	TaskDetailsDTO parentTaskDetails = getParentTaskDetails(task);
            String accountHolderName = getAccountHolderName(documentsRequest, parentTaskDetails);
            difference.setAccountHolderName(accountHolderName);
            String accountName = getAccountName(parentTaskDetails);
            difference.setAccountName(accountName);
            setAccountHolderTaskRequestInfoAndGenerateEvent(task, documentsRequest.getAccountFullIdentifier(),
                accountHolderName);
        } else if (documentsRequest.getType().equals(DocumentsRequestType.USER)) {
            difference.setUserUrid(documentsRequest.getRecipientUrid());
            setAuthorisedRepresentativeTaskRequestInfoAndGenerateEvent(documentsRequest, task);
        }
        task.setDifference(mapper.convertToJson(difference));

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        String comment = documentsRequest.getComment();
        if (documentsRequest.getComment() == null) {
            comment = "The task was automatically assigned.";
        }

        taskService.assign(List.of(task.getRequestId()), documentsRequest.getRecipientUrid(), comment);
        return task.getRequestId();
    }

    /**
     * Generates the necessary task info for Account Holder
     * submit documents request and the account event.
     *
     * @param task the task info
     */
    private void setAccountHolderTaskRequestInfoAndGenerateEvent(Task task, String accountFullIdentifier,
                                                                 String accountHolderName) {
        task.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        String action = "Documents requested for Account Holder.";
        String accountIdentifier = null;
        if (task.getParentTask() == null) {
            Account account = accountService.getAccountFullIdentifier(accountFullIdentifier);
            //TODO implement this as a Rule engine business rule. See JIRA:4527 - BR9
            if (AccountStatus.TRANSFER_PENDING.equals(account.getAccountStatus())) {
                throw AccountActionException
                    .create(AccountActionError
                        .builder()
                        .message("Request cannot be initiated for accounts in status Transfer Pending")
                        .build());
            }
            accountIdentifier = account.getIdentifier().toString();
            task.setAccount(account);

        }
        eventService.createAndPublishEvent(accountIdentifier, task.getInitiatedBy().getUrid(), accountHolderName,
            EventType.ACCOUNT_TASK_REQUESTED, action);
    }
	
	private TaskDetailsDTO getParentTaskDetails(Task task) {
		Task parentTask = task.getParentTask();
		if (parentTask == null) {
			return null;
		}
		return taskService.getTaskDetails(parentTask.getRequestId());
	}

    /**
     * Get account holder name needed for the event. If the task is installation transfer with a new account holder
     * no identifier exists in the document request. Therefore we have to get the account holder name from the parent
     * task which for now is only an account Opening with installation transfer task.
     * To be extended when the main Account Opening task will be refactored under
     *
     * @param documentsRequest
     * @param task
     * @return
     */
    private String getAccountHolderName(DocumentsRequestDTO documentsRequest, TaskDetailsDTO parentTaskDetails) {
        String accountHolderName = null;
        if (documentsRequest.getAccountHolderIdentifier() != null) {
            AccountHolderDTO accountHolder = accountHolderService
                .getAccountHolder(documentsRequest.getAccountHolderIdentifier());
            if (accountHolder == null) {
                throw AccountActionException
                    .create(AccountActionError
                        .builder()
                        .message("Account Holder does not exist for this identifier")
                        .build());
            }
            accountHolderName = accountHolder.actualName();
        } else {
            if (parentTaskDetails == null) {
                throw AccountActionException
                    .create(AccountActionError
                        .builder()
                        .message("Account Holder Identifier should always be present when no parent task is involved ")
                        .build());
            }
            if (!(parentTaskDetails instanceof AccountOpeningTaskDetailsDTO ||
            		parentTaskDetails instanceof AccountTransferTaskDetailsDTO)) {
                throw AccountActionException
                    .create(AccountActionError
                        .builder()
                        .message("Only account Opening or account Transfer parent task supported. ")
                        .build());
            }
            if (parentTaskDetails instanceof AccountOpeningTaskDetailsDTO accountOpeningTaskDetailsDTO) {
                AccountDTO proposedAccount = accountOpeningTaskDetailsDTO.getAccount();
                accountHolderName = proposedAccount.getAccountHolder().actualName();
            } else if (parentTaskDetails instanceof AccountTransferTaskDetailsDTO accountTransferTaskDetailsDTO) {
                AccountTransferAction action = accountTransferTaskDetailsDTO.getAction();
                accountHolderName = action.getAccountHolderDTO().getDetails().getName();
            }

        }
        return accountHolderName;
    }
    
	private String getAccountName(TaskDetailsDTO parentTaskDetails) {
		if (parentTaskDetails == null) {
			return null;
		}
        if (!(parentTaskDetails instanceof AccountOpeningTaskDetailsDTO ||
        		parentTaskDetails instanceof AccountTransferTaskDetailsDTO)) {
            throw AccountActionException
                .create(AccountActionError
                    .builder()
                    .message("Only account Opening or account Transfer parent task supported. ")
                    .build());
        }
		String accountName = null;
        if (parentTaskDetails instanceof AccountOpeningTaskDetailsDTO accountOpeningTaskDetailsDTO) {
            AccountDTO proposedAccount = accountOpeningTaskDetailsDTO.getAccount();
            accountName = proposedAccount.getAccountDetails().getName();
        } else if (parentTaskDetails instanceof AccountTransferTaskDetailsDTO accountTransferTaskDetailsDTO) {
            AccountDetailsDTO proposedAccount = accountTransferTaskDetailsDTO.getAccount();
            accountName = proposedAccount.getName();
        }
        return accountName;
	}

    /**
     * Generates the necessary task info for Authorised Representative submit documents
     * request and the user event.
     *
     * @param documentsRequest the documents request
     * @param task             the task info
     */
    private void setAuthorisedRepresentativeTaskRequestInfoAndGenerateEvent(DocumentsRequestDTO documentsRequest,
                                                                            Task task) {
        if (documentsRequest.getRecipientUrid() == null) {
            throw AccountActionException
                .create(AccountActionError
                    .builder()
                    .message("Recipient id not provided")
                    .build());

        }
        UserDTO userDTO = userService.getUser(documentsRequest.getRecipientUrid());
        if (userDTO == null) {
            throw AccountActionException
                .create(AccountActionError
                    .builder()
                    .message(String.format("Recipient for given id:%s not found", documentsRequest.getRecipientUrid()))
                    .build());
        }
        task.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        String action = "Documents requested for user.";
        eventService.createAndPublishEvent(userDTO.getUrid(), task.getInitiatedBy().getUrid(),
                getDisplayName(userDTO), EventType.USER_DOCUMENTS_REQUESTED, action);
    }
    
    private String getDisplayName(UserDTO user) {
        return !StringUtils.isEmpty(user.getAlsoKnownAs()) ? user.getAlsoKnownAs()
                : user.getFirstName() + StringUtils.SPACE + user.getLastName();
    }
}
