package gov.uk.ets.registry.api.authz.ruleengine.features.task;

import static gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleAppliance.YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskActionException;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskBusinessSecurityStoreSliceLoader {

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final TrustedAccountRepository trustedAccountRepository;
    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;
    private final Mapper mapper;

    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }

    /**
     * Loads the {@link TaskBusinessSecurityStoreSlice}.
     */
    public void load() {

        TaskBusinessSecurityStoreSlice taskBusinessSecurityStoreSlice = new TaskBusinessSecurityStoreSlice();
        if (ruleInputStore.containsKey(RuleInputType.TASK_REQUEST_ID) &&
            taskBusinessSecurityStoreSlice.getTaskBusinessRuleInfoList() == null) {
            setTaskBusinessRuleInfo(taskBusinessSecurityStoreSlice, (Long) ruleInputStore.get(RuleInputType.TASK_REQUEST_ID));
        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_FILE) &&
                taskBusinessSecurityStoreSlice.getTaskBusinessRuleInfoList() == null) {
            TaskFileDownloadInfoDTO taskFileDownloadInfoDTO = (TaskFileDownloadInfoDTO) ruleInputStore.get(RuleInputType.TASK_FILE);
            setTaskBusinessRuleInfo(taskBusinessSecurityStoreSlice, taskFileDownloadInfoDTO.getTaskRequestId());
        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_REQUEST_IDS) &&
            taskBusinessSecurityStoreSlice.getTaskBusinessRuleInfoList() == null) {
            List<TaskBusinessRuleInfo> list = new ArrayList<>();

            List<Task> allByRequestIdIn =
                taskRepository.findAllByRequestIdIn((List<Long>) ruleInputStore.get(RuleInputType.TASK_REQUEST_IDS));

            for (Task task : allByRequestIdIn) {
                TaskBusinessRuleInfo taskBusinessRuleInfo = new TaskBusinessRuleInfo();
                taskBusinessRuleInfo.setTask(task);
                setTaskInitiatorAndHisRoles(taskBusinessRuleInfo, task.getInitiatedBy().getUrid());
                list.add(taskBusinessRuleInfo);
            }
            taskBusinessSecurityStoreSlice.setTaskBusinessRuleInfoList(list);

        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_ASSIGNEE_URID) &&
            taskBusinessSecurityStoreSlice.getTaskAssignee() == null) {
            String taskAssigneeUrid = (String) ruleInputStore.get(RuleInputType.TASK_ASSIGNEE_URID);
            setTaskAssigneeAndHisRoles(taskBusinessSecurityStoreSlice, taskAssigneeUrid);
        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_COMPLETE_COMMENT) &&
            taskBusinessSecurityStoreSlice.getCompleteComment() == null) {
            taskBusinessSecurityStoreSlice
                .setCompleteComment((String) ruleInputStore.get(RuleInputType.TASK_COMPLETE_COMMENT));
        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_OUTCOME) &&
            taskBusinessSecurityStoreSlice.getTaskOutcome() == null) {
            List<TaskBusinessRuleInfo> taskBusinessRuleInfoList =
                taskBusinessSecurityStoreSlice.getTaskBusinessRuleInfoList();
            if (!taskBusinessRuleInfoList.isEmpty()) {
                List<Task> subTasks = taskRepository.findSubTasks(taskBusinessRuleInfoList.get(0).getTask().getId());
                taskBusinessRuleInfoList.get(0).setSubTasks(subTasks);
            }
            taskBusinessSecurityStoreSlice.setTaskOutcome((TaskOutcome) ruleInputStore.get(RuleInputType.TASK_OUTCOME));
        }
        if (ruleInputStore.containsKey(RuleInputType.TASK_UPDATE_ACTION) &&
            taskBusinessSecurityStoreSlice.getTaskUpdateAction() == null) {
            TaskUpdateAction action = (TaskUpdateAction) ruleInputStore.get(RuleInputType.TASK_UPDATE_ACTION);
            taskBusinessSecurityStoreSlice.setTaskUpdateAction(action);
        }

        if (ruleInputStore.containsKey(RuleInputType.TASK_REQUEST_ID)) {
            Task currentTask = taskRepository.findByRequestId((Long) ruleInputStore.get(RuleInputType.TASK_REQUEST_ID));
            var taskAccountIdentifier = Optional.ofNullable(currentTask.getAccount())
                    .map(Account::getFullIdentifier)
                    .orElse(null);
            if(taskAccountIdentifier != null){
                var trustedAccounts = trustedAccountRepository.
                        findTrustedAccountsForAccountIdAndStatuses(taskAccountIdentifier,
                                List.of(TrustedAccountStatus.PENDING_ADDITION_APPROVAL,TrustedAccountStatus.PENDING_REMOVAL_APPROVAL));
                taskBusinessSecurityStoreSlice.setLinkedPendingTrustedAccounts(trustedAccounts);
            }
        }
        businessSecurityStore.setTaskBusinessSecurityStoreSlice(taskBusinessSecurityStoreSlice);
    }

    private void setTaskBusinessRuleInfo(TaskBusinessSecurityStoreSlice taskBusinessSecurityStoreSlice, Long taskId) {
        TaskBusinessRuleInfo taskBusinessRuleInfo = new TaskBusinessRuleInfo();
        Task byRequestId = taskRepository.findByRequestId(taskId);
        if (byRequestId.getClaimedBy() != null) {
            setTaskAssigneeAndHisRoles(taskBusinessSecurityStoreSlice, byRequestId.getClaimedBy().getUrid());
        }
        setTaskInitiatorAndHisRoles(taskBusinessRuleInfo, byRequestId.getInitiatedBy().getUrid());
        taskBusinessRuleInfo.setTask(byRequestId);
        List<TaskBusinessRuleInfo> list = new ArrayList<>();

        if (byRequestId.getAccount() != null) {
            businessSecurityStore
                    .setAccount(accountRepository.findById(byRequestId.getAccount().getId()).orElseThrow(
                            () -> new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT)
                    ));
        }
        
        if (EnumSet.of(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST,
            RequestType.ACCOUNT_OPENING_REQUEST).contains(byRequestId.getType())) {
    		AccountDTO accountDto = mapper.convertToPojo(byRequestId.getDifference(), AccountDTO.class);
    		List<User> arList = accountDto.getAuthorisedRepresentatives()
					                      .stream()
					                      .map(AuthorisedRepresentativeDTO::getUrid)
					                      .map(userService::getUserByUrid)        
					                      .toList();
            taskBusinessSecurityStoreSlice.setCandidateAccountARs(arList);

            Optional.of(accountDto)
                .map(AccountDTO::getOperator)
                .map(OperatorDTO::getEmitterId)
                .map(emitterId -> accountRepository.existsByCompliantEntity_EmitterIdAndAccountStatusNot(emitterId, AccountStatus.CLOSED))
                .ifPresent(exists -> businessSecurityStore.setEmitterIdExists(exists));
        }

        if (EnumSet.of(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
            RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST,
            RequestType.MARITIME_OPERATOR_UPDATE_REQUEST).contains(byRequestId.getType())) {

            OperatorDTO operatorDTO = mapper.convertToPojo(byRequestId.getDifference(), OperatorDTO.class);
            Optional.of(operatorDTO)
                .map(OperatorDTO::getEmitterId)
                .map(emitterId -> accountRepository.existsByCompliantEntity_EmitterIdAndAccountStatusNot(emitterId, AccountStatus.CLOSED))
                .ifPresent(exists -> businessSecurityStore.setEmitterIdExists(exists));
        }

        if (byRequestId.getUser() != null) {
            String urid = byRequestId.getUser().getUrid();
            try {
                taskBusinessSecurityStoreSlice.setCandidateUserRoles(
                        authorizationService.getClientLevelRoles(userService.getUserByUrid(urid)
                                        .getIamIdentifier())
                                .stream()
                                .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                                .collect(Collectors.toList()));
            } catch (jakarta.ws.rs.NotFoundException e) {
                throw TaskActionException.create(TaskActionError.builder()
                        .code(TaskActionError.USER_NOT_FOUND_IN_KEYCLOAK_DB)
                        .urid(urid)
                        .message("User with urid " + urid + " was not found in Keycloak database.")
                        .build());
            }

        }
        list.add(taskBusinessRuleInfo);
        taskBusinessSecurityStoreSlice.setTaskBusinessRuleInfoList(list);
    }

    /**
     * Task initiator goes in task businessRuleInfo cause it may differ for bulk assignments.
     *
     * @param taskBusinessRuleInfo task data object needed to apply business ruless
     * @param urid                 the user urid.
     */
    private void setTaskInitiatorAndHisRoles(TaskBusinessRuleInfo taskBusinessRuleInfo,
                                             String urid) {
        User user = userService.getUserByUrid(urid);
        taskBusinessRuleInfo.setTaskInitiator(user);
        List<UserRole> taskInitiatorRoles = findRolesForUser(user);
        taskBusinessRuleInfo.setTaskInitiatorRoles(taskInitiatorRoles);
    }

    /**
     * Task assignee and roles goes to the slice since it is unique even for bulk assignments.
     *
     * @param taskBusinessSecurityStoreSlice the task rules slice input
     * @param urid                           the user urid
     */
    private void setTaskAssigneeAndHisRoles(TaskBusinessSecurityStoreSlice taskBusinessSecurityStoreSlice,
                                            String urid) {
        User taskAssignee = userService.getUserByUrid(urid);
        taskBusinessSecurityStoreSlice.setTaskAssignee(taskAssignee);
        List<UserRole> taskAssigneeRoles = findRolesForUser(taskAssignee);
        taskBusinessSecurityStoreSlice.setTaskAssigneeRoles(taskAssigneeRoles);
    }

    private List<UserRole> findRolesForUser(User user) {
        List<UserRole> userRoles;
        try {
            userRoles = authorizationService
                .getClientLevelRoles(user.getIamIdentifier())
                .stream()
                .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                .collect(Collectors.toList());

        } catch (jakarta.ws.rs.NotFoundException e) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.USER_NOT_FOUND_IN_KEYCLOAK_DB)
                .urid(user.getUrid())
                .message("User with urid " + user.getUrid() +
                    " was not found in Keycloak database.")
                .build());
        }
        return userRoles;
    }
}
