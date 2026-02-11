package gov.uk.ets.registry.api.account.service;

import static java.time.ZoneOffset.UTC;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.UniqueEmitterIdBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.HasAccountOpenCompleteScope;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorAdminCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsForNewAccountMustBeActiveRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.integration.service.operator.OperatorEventService;
import gov.uk.ets.registry.api.messaging.UktlAccountNotifyMessageService;
import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import gov.uk.ets.registry.api.payment.service.PaymentTaskAutoCompletionService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.AccountOpeningTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountOpeningTaskService
    implements TaskTypeService<AccountOpeningTaskDetailsDTO> {

    private final OperatorEventService operatorEventService;

    private final UserService userService;

    private final UktlAccountNotifyMessageService uktlAccountNotifyMessageService;

    private final ComplianceEventService complianceEventService;

    private final AccountOpeningUpdateTaskService accountOpeningUpdateTaskService;

    private final Mapper mapper;

    private final AccountAccessService accountAccessService;

    private final AccountCreationService accountCreationService;

    private final RequestedDocsTaskService requestedDocsTaskService;
    
    private final PaymentTaskAutoCompletionService paymentTaskAutoCompletionService;
    
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_OPENING_REQUEST);
    }

    @Override
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return accountOpeningUpdateTaskService.getRequestedTaskFile(infoDTO.getTaskRequestId());
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

    //TODO: add more business rules.eg revalidate installation permit id
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

        //UKETS-6528 Also complete child document subtasks
        requestedDocsTaskService.completeChildRequestedDocumentTasks( taskDTO.getRequestId(),taskOutcome);
        
        //Also complete any pending payment sub-tasks
        paymentTaskAutoCompletionService.completeChildRequestedPaymentTasks(taskDTO.getRequestId(), taskOutcome);
        
        if (TaskOutcome.REJECTED.equals(taskOutcome)) {
            return defaultResponseBuilder(taskDTO).build();
        }
        Account newAccount = accountCreationService.createAccountFromTask(taskDTO);
        accountOpeningUpdateTaskService.setAccountOnChildAccountHolderDocumentTasks(newAccount, taskDTO.getRequestId());
        accountAccessService.createAccountAccess(newAccount, UserRole.getRolesForRoleBasedAccess(),
                                                 AccountAccessRight.ROLE_BASED);
        sendAccountNotifications(newAccount, taskDTO.getCompletedDate());

        operatorEventService.updateOperator(newAccount.getCompliantEntity(), newAccount.getRegistryAccountType().name());

        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
        OnlySeniorAdminCanClaimTaskRule.class,
        HasAccountOpenCompleteScope.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    @Protected({
        OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
        HasAccountOpenCompleteScope.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {

    }


    /**
     * Send notifications to the transaction log and compliance calculation services
     *
     * @param account the newly created account
     */
    private void sendAccountNotifications(Account account, Date taskCompletionDate) {
        if (!RegistryAccountType.NONE.equals(account.getRegistryAccountType())) {
            uktlAccountNotifyMessageService
                .sendAccountOpeningNotification(AccountNotification.convert(account));
        }

        CompliantEntity compliantEntity = account.getCompliantEntity();
        if (compliantEntity != null) {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            CompliantEntityInitializationEvent accountCreationEvent = CompliantEntityInitializationEvent.builder()
                .firstYearOfVerifiedEmissions(compliantEntity.getStartYear())
                .lastYearOfVerifiedEmissions(compliantEntity.getEndYear())
                .actorId(userService.getCurrentUser().getUrid())
                .eventId(UUID.randomUUID())
                .compliantEntityId(compliantEntity.getIdentifier())
                .dateTriggered(now)
                .dateRequested(LocalDateTime.ofInstant(taskCompletionDate.toInstant(), UTC))
                .currentYear(now.getYear())
                .build();
            complianceEventService.processEvent(accountCreationEvent);
        }
    }
}
