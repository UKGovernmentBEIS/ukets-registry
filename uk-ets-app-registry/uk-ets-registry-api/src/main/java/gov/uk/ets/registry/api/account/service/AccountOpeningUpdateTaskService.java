package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.service.pdf.AccountOpeningPdfGenerator;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskServiceException;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class AccountOpeningUpdateTaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final EventService eventService;

    private final AccountConversionService accountConversionService;

    private final AccountHolderRepository holderRepository;

    private final Mapper mapper;

    private final AccountHolderFileRepository accountHolderFileRepository;

    private final AccountHolderService accountHolderService;

    private final UserStatusService userStateService;

    private final AccountOpeningPdfGenerator pdfGenerator;

    public UploadedFile getRequestedTaskFile(Long requestId) {
        Task task = taskRepository.findByRequestId(requestId);
        UploadedFile file = new UploadedFile();
        file.setFileData(pdfGenerator.generate(task));
        file.setFileName("Account_opening_request_" + requestId + ".pdf");
        return file;
    }

    @Transactional
    public TaskDetailsDTO updateTask(String updateInfo,
                                     TaskDetailsDTO taskDetails,
                                     TaskUpdateAction taskUpdateAction) {

        User currentUser = userService.getCurrentUser();
        taskDetails.setCurrentUserClaimant(
            currentUser.getUrid().equals(taskDetails.getClaimantURID()));

        Task task = taskRepository.findByRequestId(taskDetails.getRequestId());
        AccountDTO accountDTO = mapper.convertToPojo(taskDetails.getDifference(), AccountDTO.class);
        String action;
        String description;
        String oldAccountHolderName =
            accountDTO.getOldAccountHolder() != null ? accountDTO.getOldAccountHolder().actualName() : "";

        switch (taskUpdateAction) {
            case CHANGE_REGULATOR:
                accountDTO.getOperator().setChangedRegulator(changeRegulator(accountDTO, updateInfo));

                action = "Edit - Regulator changed.";
                description = "New regulator: " + accountDTO.getOperator().getChangedRegulator().name();
                eventService
                    .createAndPublishEvent(taskDetails.getRequestId().toString(), currentUser.getUrid(),
                        description,
                        EventType.TASK_EDIT, action);
                break;
            case UPDATE_ACCOUNT_HOLDER:
                AccountHolder holder;
                try {
                    Long updateAccountHolderId = Long.parseLong(updateInfo);
                    holder = holderRepository.getAccountHolder(updateAccountHolderId);
                    if (holder == null) {
                        throw TaskServiceException.create(TaskActionError.builder()
                            .code(TaskActionError.ACCOUNT_HOLDER_NON_EXISTENT)
                            .message("The account holder id you entered does not exist.")
                            .build());
                    }
                    accountDTO.setChangedAccountHolderId(updateAccountHolderId);
                } catch (NumberFormatException exception) {
                    throw TaskServiceException.create(TaskActionError.builder()
                        .code(TaskActionError.ACCOUNT_HOLDER_NON_EXISTENT)
                        .message("The account holder id you entered does not exist.")
                        .build());
                }

                AccountHolderDTO accountHolderDTO = updateAccountHolder(holder);
                accountHolderDTO.setId(null);
                accountDTO.setAccountHolder(accountHolderDTO);
                accountDTO.setAccountHolderContactInfo(updateAccountHolderContactInfo(holder));

                action = "Update - Account Holder - Existing Task";
                description = String.format("Task RequestId %d : AH change from %s to %d", taskDetails.getRequestId(),
                    accountDTO.getOldAccountHolder().getId() != null ?
                        accountDTO.getOldAccountHolder().getId().toString() : oldAccountHolderName,
                    accountDTO.getChangedAccountHolderId());

                eventService.createAndPublishEvent(String.valueOf(taskDetails.getRequestId()), currentUser.getUrid(),
                    description, EventType.TASK_EDIT, action);
                break;
            case RESET_ACCOUNT_HOLDER:
                accountDTO.setChangedAccountHolderId(null);
                accountDTO.setAccountHolder(accountDTO.getOldAccountHolder());
                accountDTO.setAccountHolderContactInfo(accountDTO.getOldAccountHolderContactInfo());
                action = "Reset - Account Holder - Existing Task";
                description = String.format("Task RequestId %d : AH reset to %s", taskDetails.getRequestId(),
                    accountDTO.getAccountHolder().getId() != null ? accountDTO.getAccountHolder().getId().toString() :
                        oldAccountHolderName);

                eventService.createAndPublishEvent(String.valueOf(taskDetails.getRequestId()), currentUser.getUrid(),
                    description, EventType.TASK_EDIT, action);
                break;
            case RESET_REGULATOR:
                RegulatorType originalRegulatorInRequest =
                    OperatorType.INSTALLATION_TRANSFER.name().equals(accountDTO.getOperator().getType()) ?
                        accountDTO.getInstallationToBeTransferred().getRegulator() :
                        accountDTO.getOperator().getRegulator();
                accountDTO.getOperator()
                    .setChangedRegulator(originalRegulatorInRequest);

                action = "Edit - Regulator reset.";
                description = "Regulator reset to: " + originalRegulatorInRequest.name();
                eventService
                    .createAndPublishEvent(taskDetails.getRequestId().toString(), currentUser.getUrid(),
                        description,
                        EventType.TASK_EDIT, action);
        }

        taskDetails.setDifference(mapper.convertToJson(accountDTO));
        task.setDifference(taskDetails.getDifference());

        return taskDetails;
    }

    public List<KeycloakUser> getARUpdatedInfo(List<AuthorisedRepresentativeDTO> authorisedRepresentatives) {
        List<KeycloakUser> ars = new ArrayList<>();
        if (!CollectionUtils.isEmpty(authorisedRepresentatives)) {
            authorisedRepresentatives.forEach(ar -> ars.add(userStateService.getKeycloakUser(ar.getUrid())));
        }
        return ars;
    }

    private RegulatorType changeRegulator(AccountDTO dto, String updateInfo) {
        try {
            return RegulatorType.valueOf(updateInfo);
        } catch (IllegalArgumentException exception) {
            return dto.getInstallationToBeTransferred().getRegulator();
        }
    }

    private AccountHolderContactInfoDTO updateAccountHolderContactInfo(AccountHolder holder) {
        return accountHolderService.getAccountHolderContactInfo(holder.getIdentifier());
    }

    private AccountHolderDTO updateAccountHolder(AccountHolder holder) {
        return accountConversionService.convert(holder);
    }

    /**
     * When completing the account all previously child tasks requesting documents
     * for the account holder should be associated with the new account
     *
     * @param newAccount
     * @param parentTaskId
     */
    public void setAccountOnChildAccountHolderDocumentTasks(Account newAccount, Long parentTaskId) {
        List<Task> subTasks = taskRepository.findSubTasksParentRequestId(parentTaskId);
        subTasks.stream().filter(childAccountHolderUploadTask -> childAccountHolderUploadTask.getType()
            .equals(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)).forEach(childTask ->
        {
            RequestDocumentsTaskDifference requestDocumentsTaskDifference =
                mapper.convertToPojo(childTask.getDifference(), RequestDocumentsTaskDifference.class);
            if (matchAccountHolderOnParentAndChildTask(newAccount, requestDocumentsTaskDifference)) {
                childTask.setAccount(newAccount);

                Map<String, Long> uploadedFileNameIdMap = requestDocumentsTaskDifference.getUploadedFileNameIdMap();
                /**
                 * The null check here handles documents uploaded before UKETS-4769. In this case the files where already uploaded
                 * in the account holder files tables even before the task completion so no furnther actions are required
                 */
                if (requestDocumentsTaskDifference.getUploadedFileNameIdMap() != null) {
                    uploadedFileNameIdMap.keySet().forEach(k -> {
                        //First check if an entry already exists , this is the case when the new account has an already existing Holder. JIRA UKETS-5961
                        // the null check here handles the scenario where the user replied that he could not upload a document
                        if (Optional.ofNullable(accountHolderFileRepository.findByIdAndAccountHolder(uploadedFileNameIdMap.get(k), newAccount.getAccountHolder())).isEmpty() && 
                            uploadedFileNameIdMap.get(k) != null) {
                            accountHolderFileRepository.insertAccountHolderFile(uploadedFileNameIdMap.get(k), k,
                                newAccount.getAccountHolder().getId());
                        }
                    });
                }
            }
        });


    }

    /**
     * The account holder on the child task will have an id only if existed in the database at the time of the new account was requeste
     * If the account holder did not exist at the time of the document request because the user submitted a new account holder
     * when requested the account, then the only way to match them on task completion is by comparing the names
     *
     * @param newAccount            the newly created account
     * @param childDocumentTaskDiff the information that exists on the child request document task
     * @return
     */
    private boolean matchAccountHolderOnParentAndChildTask(Account newAccount,
                                                           RequestDocumentsTaskDifference childDocumentTaskDiff) {

        if (childDocumentTaskDiff.getAccountHolderIdentifier() != null) {
            return childDocumentTaskDiff.getAccountHolderIdentifier()
                .equals(newAccount.getAccountHolder().getIdentifier());
        } else {
            return newAccount.getAccountHolder().actualName().equals(childDocumentTaskDiff.getAccountHolderName());
        }
    }
}
