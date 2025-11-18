package gov.uk.ets.registry.api.account.service;

import static java.time.ZoneOffset.UTC;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorAdminCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsForNewAccountMustBeActiveRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.UniqueEmitterIdBusinessRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.GetCurrentDynamicStatusEvent;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.integration.service.operator.OperatorEventService;
import gov.uk.ets.registry.api.messaging.UktlAccountNotifyMessageService;
import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.AccountOpeningTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.sis.internal.util.StandardDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountOpeningWithInstallationTransferTaskService
    implements TaskTypeService<AccountOpeningTaskDetailsDTO> {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final UserService userService;

    private final EventService eventService;

    private final UktlAccountNotifyMessageService uktlAccountNotifyMessageService;

    private final TransferValidationService transferValidationService;

    private final Mapper mapper;

    private final AccountOpeningUpdateTaskService accountOpeningUpdateTaskService;

    private final ComplianceEventService complianceEventService;

    private final AccountAccessService accountAccessService;

    private final AccountCreationService accountCreationService;
    
    private final RequestedDocsTaskService requestedDocsTaskService;

    private final OperatorEventService operatorEventService;

    @Override
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return accountOpeningUpdateTaskService.getRequestedTaskFile(infoDTO.getTaskRequestId());
    }

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST);
    }

    @Override
    public AccountOpeningTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        AccountOpeningTaskDetailsDTO response =
            new AccountOpeningTaskDetailsDTO(taskDetailsDTO);
        response.setAccount(mapper.convertToPojo(taskDetailsDTO.getDifference(), AccountDTO.class));
        response.setUserDetails(accountOpeningUpdateTaskService
            .getARUpdatedInfo(response.getAccount().getAuthorisedRepresentatives()));
        return response;
    }

    @Override
    @Transactional
    @SneakyThrows
    public AccountOpeningTaskDetailsDTO updateTask(String updateInfo,
                                                   TaskDetailsDTO taskDetails,
                                                   TaskUpdateAction taskUpdateAction) {

        var taskDetailsDTO = accountOpeningUpdateTaskService.updateTask(updateInfo, taskDetails, taskUpdateAction);
        return getDetails(taskDetailsDTO);
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            ARsForNewAccountMustBeActiveRule.class,
            UniqueEmitterIdBusinessRule.class
        }
    )
    @Override
    @Transactional
    @EmitsGroupNotifications({GroupNotificationType.ACCOUNT_OPENING_FINALISATION})
    public TaskCompleteResponse complete(AccountOpeningTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
                                         String comment) {

        Optional<Account> oldAccount = accountRepository
            .findByCompliantEntityIdentifier(taskDTO.getAccount().getInstallationToBeTransferred().getIdentifier());

        if (oldAccount.isEmpty()) {
            throw new IllegalStateException("Old account not found in DB.");
        }

        //UKETS-6528 Also complete child document subtasks
        requestedDocsTaskService.completeChildRequestedDocumentTasks(taskDTO.getRequestId(),taskOutcome);
        
        if (TaskOutcome.REJECTED.equals(taskOutcome)) {
            oldAccount.get().setAccountStatus(taskDTO.getAccount().getOldAccountStatus());
            return defaultResponseBuilder(taskDTO).build();
        }
        //Transfer-BR3
        transferValidationService.validatePendingTransactions(oldAccount.get().getIdentifier());

        //Transfer-BR4
        if(!taskDTO.getAccount().getOperator().getPermit().getPermitIdUnchanged()) {
            accountService.validatePermitId(taskDTO.getAccount().getOperator().getPermit().getId());
        }

        Account newAccount = accountCreationService.createAccountFromTask(taskDTO);
        oldAccount.get().setCompliantEntity(null);
        oldAccount.get().setClosingDate(new Date());
        propagateEventToAccounts(oldAccount.get(), newAccount, userService.getCurrentUser());
        accountOpeningUpdateTaskService.setAccountOnChildAccountHolderDocumentTasks(newAccount, taskDTO.getRequestId());
        accountAccessService.createAccountAccess(newAccount, UserRole.getRolesForRoleBasedAccess(),
                                                 AccountAccessRight.ROLE_BASED);
        sendAccountNotification(oldAccount.get(), newAccount, taskDTO.getCompletedDate());

        operatorEventService.updateOperator(newAccount.getCompliantEntity(), newAccount.getRegistryAccountType().name());
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
        OnlySeniorAdminCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    @Protected({
        OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {

    }


    private void sendAccountNotification(Account oldAccount, Account newAccount, Date taskCompletionDate) {
        if (!RegistryAccountType.NONE.equals(newAccount.getRegistryAccountType())) {
            AccountNotification accountNotification = AccountNotification.convert(newAccount);
            accountNotification.setOldIdentifier(oldAccount.getIdentifier());
            uktlAccountNotifyMessageService.sendAccountOpeningNotification(accountNotification);

            CompliantEntity compliantEntity = newAccount.getCompliantEntity();
            if (compliantEntity != null) {
                LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
                GetCurrentDynamicStatusEvent getCurrentDynamicStatusEvent = GetCurrentDynamicStatusEvent.builder()
                    .actorId(userService.getCurrentUser().getUrid())
                    .compliantEntityId(compliantEntity.getIdentifier())
                    .dateTriggered(now)
                    .dateRequested(LocalDateTime.ofInstant(taskCompletionDate.toInstant(), UTC))
                    .build();
                complianceEventService.processEvent(getCurrentDynamicStatusEvent);
            }
        }
    }

    private void propagateEventToAccounts(Account oldAccount, Account newAccount, User currentUser) {
        String action = "Installation transferred";
        String description = String
            .format("Old account number: %s , New account number: %s", oldAccount.getFullIdentifier(),
                newAccount.getFullIdentifier());
        eventService
            .createAndPublishEvent(String.valueOf(oldAccount.getIdentifier()), currentUser.getUrid(),
                description,
                EventType.ACCOUNT_INSTALLATION_TRANSFER_COMPLETED, action);
        eventService
            .createAndPublishEvent(String.valueOf(newAccount.getIdentifier()), currentUser.getUrid(),
                description,
                EventType.ACCOUNT_INSTALLATION_TRANSFER_COMPLETED, action);
    }
}
