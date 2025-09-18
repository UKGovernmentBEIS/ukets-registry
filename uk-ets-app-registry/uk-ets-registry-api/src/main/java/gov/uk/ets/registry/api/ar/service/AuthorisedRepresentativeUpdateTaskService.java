package gov.uk.ets.registry.api.ar.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ARsCanBeOnlyNonSuspendedUser;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.AccountARsLimitShouldNotBeExceededRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanRejectTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskARStatus;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskARStatusRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.AuthoriseRepresentativeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthorisedRepresentativeUpdateTaskService
    implements TaskTypeService<AuthoriseRepresentativeTaskDetailsDTO> {

    private final AccountService accountService;
    private final UserConversionService userConversionService;
    private final UserAdministrationService userAdministrationService;
    private final AccountAccessRepository accountAccessRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final UserStatusService userStateService;
    private final RequestedDocsTaskService requestedDocsTaskService;
    private final Mapper mapper;
    private final TaskARStatusRepository taskARStatusRepository;
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST, // TODO: Remove restore/suspend as the action does not require task
            RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST);
    }

    @Override
    public AuthoriseRepresentativeTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        AuthoriseRepresentativeTaskDetailsDTO result = new AuthoriseRepresentativeTaskDetailsDTO(taskDetailsDTO);
        result.setAccountInfo(accountService.getAccountInfo(Long.valueOf(taskDetailsDTO.getAccountNumber())));
        if (result.getReferredUserURID() != null) {
            result.setUserDetails(List.of(userStateService.getKeycloakUser(result.getReferredUserURID())));
        }
        extractAndSetTaskDifferenceInfo(result);
        return result;
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class,
            OnlySeniorRegistryAdminCanRejectTaskRule.class,
            OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval.class,
            AccountARsLimitShouldNotBeExceededRule.class,
            ARsCanBeOnlyNonSuspendedUser.class
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(AuthoriseRepresentativeTaskDetailsDTO taskDTO,
                                         TaskOutcome taskOutcome, String comment) {

        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            Account account = extractAccountEntity(taskDTO);
            proceedWithApproveActions(account, taskDTO);
        }

        Task task = taskRepository.findByRequestId(taskDTO.getRequestId());
        User user = taskDTO.getArUpdateType().equals(ARUpdateActionType.REMOVE) ||
                taskDTO.getArUpdateType().equals(ARUpdateActionType.CHANGE_ACCESS_RIGHTS)
                ? userService.getUserByUrid(taskDTO.getCurrentUser().getUrid())
                : userService.getUserByUrid(taskDTO.getNewUser().getUrid());
        saveUserStatusOnTaskCompletion(task,user);

        //UKETS-6528 Also complete child document subtasks
        requestedDocsTaskService.completeChildRequestedDocumentTasks(taskDTO.getRequestId(),taskOutcome);
        
        return defaultResponseBuilder(taskDTO).build();
    }

    private void proceedWithApproveActions(Account account,
                                           AuthoriseRepresentativeTaskDetailsDTO taskDTO) {
        User currentUser;
        Task parentTask;
        switch (taskDTO.getTaskType()) {
            case AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST:
                setNewAccountAccess(getARAccount(account, taskDTO.getNewUser().getUrid()), account,
                    taskDTO.getNewUser().getUrid(),
                    taskDTO.getArUpdateAccessRight());
                User authorizedRepresentative = userService.getUserByUrid(taskDTO.getNewUser().getUrid());
                currentUser = userService.getCurrentUser();
                parentTask = taskRepository.findByRequestId(taskDTO.getRequestId());
                userService.validateUserAndGenerateEvents(authorizedRepresentative, currentUser, parentTask);
                break;
            case AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST:
                setAccountAccessState(getARAccount(account, taskDTO.getCurrentUser().getUrid()),
                    AccountAccessState.REMOVED);
                authorizedRepresentativeService
                    .removeKeycloakRoleIfNoOtherAccountAccess(taskDTO.getCurrentUser().getUrid(),
                        taskDTO.getCurrentUser().getUser().getKeycloakId());
                break;
            case AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST: // TODO: Remove suspend as the action does not require task
                setAccountAccessState(getARAccount(account, taskDTO.getCurrentUser().getUrid()),
                    AccountAccessState.SUSPENDED);
                break;
            case AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST:
                AccountAccess update = getARAccount(account, taskDTO.getCurrentUser().getUrid());
                if (update == null) {
                    throw new UkEtsException(
                        "Expecting the account access of the Authorise representative");
                }
                update.setRight(taskDTO.getArUpdateAccessRight());
                break;
            case AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST: // TODO: Remove restore as the action does not require task
                AccountAccess restore = getARAccount(account, taskDTO.getCurrentUser().getUrid());
                setAccountAccessState(restore, AccountAccessState.ACTIVE);
                break;
            case AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST:
                AccountAccess replace = getARAccount(account, taskDTO.getNewUser().getUrid());
                setNewAccountAccess(replace, account, taskDTO.getNewUser().getUrid(),
                    taskDTO.getArUpdateAccessRight());
                AccountAccess replaceeAccountAccess = getARAccount(account, taskDTO.getCurrentUser().getUrid());
                if (replaceeAccountAccess == null) {
                    throw new UkEtsException(
                        String.format(
                            "Missing AccountAccess entry when replacing ARS for account id:%s and user urid:%s",
                            account.getId(), taskDTO.getCurrentUser().getUrid()));
                }
                replaceeAccountAccess.setState(AccountAccessState.REMOVED);
                authorizedRepresentativeService
                    .removeKeycloakRoleIfNoOtherAccountAccess(taskDTO.getCurrentUser().getUrid(),
                        taskDTO.getCurrentUser().getUser().getKeycloakId());
                authorizedRepresentative = userService.getUserByUrid(taskDTO.getNewUser().getUrid());
                currentUser = userService.getCurrentUser();
                parentTask = taskRepository.findByRequestId(taskDTO.getRequestId());
                userService.validateUserAndGenerateEvents(authorizedRepresentative, currentUser, parentTask);
                break;
            default:
                break;
        }
    }

    /**
     * Runs on {@link RequestType#AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST}
     * and {@link RequestType#AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST} .
     *
     * @param accountAccess can be null or already existing for the account
     * @param account       the related account
     * @param urid          the urid to set the user in the account access if it does not already exis
     * @param right         the new right
     */
    private void setNewAccountAccess(AccountAccess accountAccess, Account account, String urid,
                                     AccountAccessRight right) {
        if (accountAccess == null) {
            accountAccess = new AccountAccess();
            accountAccess.setAccount(account);
            accountAccess.setUser(userService.getUserByUrid(urid));
        }
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(right);
        if (accountAccess.getId() == null) {
            accountAccessRepository.save(accountAccess);
        }
    }

    @Protected({
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    private void setAccountAccessState(AccountAccess accountAccess, AccountAccessState accountAccessState) {
        if (accountAccess == null) {
            throw new UkEtsException(
                "Expecting the account access of the Authorise representative");
        }
        accountAccess.setState(accountAccessState);
    }

    private Account extractAccountEntity(
        AuthoriseRepresentativeTaskDetailsDTO authoriseRepresentativeTaskDetailsDTO) {
        Account account = accountService
            .getAccount(authoriseRepresentativeTaskDetailsDTO.getAccountInfo().getIdentifier());
        if (account == null) {
            throw new UkEtsException("Expecting exactly one account");
        }
        return account;
    }

    private AccountAccess getARAccount(Account account, String urid) {
        Optional<AccountAccess> accountAccessOpt = account.getAccountAccesses().stream()
            .filter(aa -> aa.getUser().getUrid().equals(urid))
            .findFirst();
        return accountAccessOpt.orElse(null);
    }

    /**
     * Extracts authorise representative dto from task and sets properties of taskDTO.
     *
     * @param taskDetailsDTO a taskDTO for Authorise Representative update operation.
     */
    private void extractAndSetTaskDifferenceInfo(
        AuthoriseRepresentativeTaskDetailsDTO taskDetailsDTO) {

        AuthorisedRepresentativeDTO newAr = new AuthorisedRepresentativeDTO();
        ARUpdateAction arUpdateAction = mapper.convertToPojo(taskDetailsDTO.getDifference(), ARUpdateAction.class);
        taskDetailsDTO.setArUpdateType(arUpdateAction.getType());
        taskDetailsDTO.setArUpdateAccessRight(arUpdateAction.getAccountAccessRight());
        Account account = extractAccountEntity(taskDetailsDTO);
        AccountAccess accountAccess = getARAccount(account, arUpdateAction.getUrid());
        newAr
            .setRight(accountAccess != null ? accountAccess.getRight() : arUpdateAction.getAccountAccessRight());
        newAr.setState(accountAccess != null ? accountAccess.getState() : null);
        User userEntity = userService.getUserByUrid(arUpdateAction.getUrid());
        Task task = taskRepository.findByRequestId(taskDetailsDTO.getRequestId());
        Optional<TaskARStatus> optionalTaskARStatus =
                taskARStatusRepository.findByTaskAndUser(task,userEntity);
        UserDTO user = userConversionService.convert(userEntity);
        // Adds the last user state when the task was completed if it exists.
        optionalTaskARStatus.ifPresent(taskARStatus -> user.setStatus(taskARStatus.getState()));
        newAr.setUser(user);
        newAr.setUrid(user.getUrid());
        ContactDTO workContact = userAdministrationService.findWorkContactDetailsByIamId(user.getKeycloakId());
        newAr.setContact(workContact);
        if (ARUpdateActionType.ADD.equals(arUpdateAction.getType()) ||
            ARUpdateActionType.REPLACE.equals(arUpdateAction.getType())
        ) {
            taskDetailsDTO.setNewUser(newAr);
        }
        if (ARUpdateActionType.REMOVE.equals(arUpdateAction.getType()) ||
            ARUpdateActionType.SUSPEND.equals(arUpdateAction.getType()) ||
            ARUpdateActionType.CHANGE_ACCESS_RIGHTS.equals(arUpdateAction.getType()) ||
            ARUpdateActionType.RESTORE.equals(arUpdateAction.getType())) {
            taskDetailsDTO.setCurrentUser(newAr);
        }
        if (ARUpdateActionType.REPLACE.equals(arUpdateAction.getType())) {
            taskDetailsDTO.setCurrentUser(getAuthorizedRepresentative(account, arUpdateAction.getToBeReplacedUrid()));
        }
    }

    //TODO: set also newAr authorised representatives with the same method
    private AuthorisedRepresentativeDTO getAuthorizedRepresentative(Account account, String urid) {
        AuthorisedRepresentativeDTO toBeReplaced = new AuthorisedRepresentativeDTO();
        UserDTO toBeReplacedUserDTO =
            userConversionService.convert(userService.getUserByUrid(urid));
        toBeReplaced.setUser(toBeReplacedUserDTO);
        toBeReplaced.setUrid(urid);
        toBeReplaced
            .setContact(userAdministrationService.findWorkContactDetailsByIamId(toBeReplacedUserDTO.getKeycloakId()));
        AccountAccess currentAccountAccess = getARAccount(account, urid);
        if (currentAccountAccess == null) {
            throw new UkEtsException(
                String.format(
                    "Missing AccountAccess entry when replacing ARS for account id:%s and user urid:%s",
                    account.getId(), urid));
        }
        toBeReplaced.setRight(currentAccountAccess.getRight());
        toBeReplaced.setState(currentAccountAccess.getState());
        return toBeReplaced;
    }

    private void saveUserStatusOnTaskCompletion(Task task, User authorizedRepresentative) {
        TaskARStatus taskARStatus = new TaskARStatus();
        taskARStatus.setTask(task);
        taskARStatus.setUser(authorizedRepresentative);
        taskARStatus.setState(authorizedRepresentative.getState());
        taskARStatusRepository.save(taskARStatus);
    }
}
