package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Resource;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.JuniorAdminCannotBeAssignedToTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskActionException;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.web.model.TransactionProposalCompleteResponse;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class TransactionProposalTaskService implements TaskTypeService<AllTransactionTaskDetailsDTO> {
    
    public static final String TRANSACTION_PROPOSAL_TASK = "Transaction proposal task";
    public static final String TRANSACTION_PROPOSAL_TASK_COMPLETED = "Transaction proposal task completed";

    private final TransactionService transactionService;

    private final AuthorizationService authorizationService;

    private final UserService userService;

    private final AccountAuthorizationService accountAuthorizationService;

    private final TransactionWithTaskService transactionWithTaskService;

    private final EventService eventService;

    private final EnumMap<TransactionType, TransactionTypeTaskService>
        transactionTypeTransactionTypeTaskServiceEnumMap = new EnumMap<>(TransactionType.class);

    /**
     * Constructor and registration of transaction specific services.
     */
    public TransactionProposalTaskService(TransactionService transactionService,
                                          AuthorizationService authorizationService,
                                          UserService userService,
                                          AccountAuthorizationService accountAuthorizationService,
                                          EventService eventService,
                                          Set<TransactionTypeTaskService> transactionTypeTaskServices,
                                          TransactionWithTaskService transactionWithTaskService
    ) {
        this.transactionService = transactionService;
        this.authorizationService = authorizationService;
        this.userService = userService;
        this.accountAuthorizationService = accountAuthorizationService;
        this.eventService = eventService;
        this.transactionWithTaskService = transactionWithTaskService;
        registerTransactionTypeServices(transactionTypeTaskServices);
    }


    /**
     * Registers all task specific services in a map.
     *
     * @param taskTypeServices the task specific services
     */
    private void registerTransactionTypeServices(Set<TransactionTypeTaskService> taskTypeServices) {
        log.info("Registering task specific services");
        taskTypeServices.forEach(taskTypeService -> {
            Set<TransactionType> set = taskTypeService.appliesFor();
            set.forEach(transactionType -> {
                if (transactionTypeTransactionTypeTaskServiceEnumMap.containsKey(transactionType)) {
                    throw new UkEtsException(
                        String.format("Transaction task service already registered for:%s", transactionType));
                } else {
                    log.info("Registering service for transaction type:{} -> {}", transactionType, taskTypeService);
                    transactionTypeTransactionTypeTaskServiceEnumMap.put(transactionType, taskTypeService);
                }
            });
        });
    }

    /**
     * Select a specific service to perform action for tasks based on task Type.
     *
     * @param transactionType the task request type
     * @return
     */
    private Optional<TransactionTypeTaskService> getTaskService(TransactionType transactionType) {
        TransactionTypeTaskService transactionTypeTaskService =
            transactionTypeTransactionTypeTaskServiceEnumMap.get(transactionType);
        if (transactionTypeTaskService != null) {
            return Optional.of(transactionTypeTaskService);
        }
        return Optional.empty();
    }

    /**
     * Retrieves the task details for a Transaction Request task.
     */
    @Override
    public AllTransactionTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        //TODO Replace the hardcoded first Transaction with something more dynamic
        Transaction transaction = transactionService
            .getTransactionWithBlocks(taskDetailsDTO.getTransactionIdentifiers().stream().findFirst().orElseThrow());
        Optional<TransactionTypeTaskService> transactionTypeTaskServiceOptional = getTaskService(transaction.getType());
        if (transactionTypeTaskServiceOptional.isPresent()) {
            TransactionTypeTaskService transactionTypeTaskService = transactionTypeTaskServiceOptional.get();
            return transactionTypeTaskService
                .getDetails(new AllTransactionTaskDetailsDTO(taskDetailsDTO, transaction.getType(), transaction.getReference()), transaction);
        }
        checkTransactionProposalTaskReadAccessRights(transaction.getTransferringAccount().getAccountIdentifier(),
            transaction.getAcquiringAccount().getAccountIdentifier());
        return transactionWithTaskService.getTransactionTaskDetails(taskDetailsDTO, transaction);
    }


    @Override
    public void claim(Task task) {
        // implemented for being able to apply permissions using annotations
        List<TaskTransaction> taskTransactions = task.getTransactionIdentifiers().stream().collect(Collectors.toList());
        for (TaskTransaction taskTransaction : taskTransactions) {
            Transaction transaction = transactionService.getTransactionWithBlocks(taskTransaction.getTransactionIdentifier());
            Optional<TransactionTypeTaskService> transactionTypeTaskServiceOptional =
                getTaskService(transaction.getType());
            if (transactionTypeTaskServiceOptional.isPresent()) {
                transactionTypeTaskServiceOptional.get().checkForInvalidClaimantPermissions();
            }            
        }
    }

    @Override
    public void assign(Task task) {
        // implemented for being able to apply permissions using annotations
        List<TaskTransaction> taskTransactions = task.getTransactionIdentifiers().stream().collect(Collectors.toList());
        for (TaskTransaction taskTransaction : taskTransactions) {
            Transaction transaction = transactionService.getTransactionWithBlocks(taskTransaction.getTransactionIdentifier());
            Optional<TransactionTypeTaskService> transactionTypeTaskServiceOptional =
                getTaskService(transaction.getType());
            if (transactionTypeTaskServiceOptional.isPresent()) {
                transactionTypeTaskServiceOptional.get().checkForInvalidAssignPermissions();
            }            
        }
    }


    /**
     * Finalises a transaction.
     *
     * @param taskDetailsDTO The transaction TaskDetailsDTO.
     * @param outcome        The task outcome.
     * @return a response.
     */
    @Transactional
    @Override
    public TransactionProposalCompleteResponse complete(AllTransactionTaskDetailsDTO taskDetailsDTO,
                                                        TaskOutcome outcome,
                                                        String comment) {
        String transactionIdentifier = getTransactionIdentifier(taskDetailsDTO);
        
        this.checkTransactionProposalTaskCompleteAccessRights(outcome, taskDetailsDTO.getInitiatorId(),
            transactionIdentifier, taskDetailsDTO.getAccountNumber());


        String action = "Transaction proposal task completed";
        User currentUser = userService.getCurrentUser();
        eventService.createAndPublishEvent(transactionIdentifier, currentUser.getUrid(), outcome.name(),
            EventType.TRANSACTION_TASK_COMPLETED, action);
        if (comment != null) {
            eventService.createAndPublishEvent(transactionIdentifier, currentUser.getUrid(), comment,
                EventType.TRANSACTION_TASK_COMMENT, String.format("%s (comment)", action));
        }
        //finalise transaction and publish event (if delayed) AFTER the task approval event
        BusinessCheckResult finaliseResult = transactionService.finaliseTransaction(transactionIdentifier, outcome,
            authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN));

        if (TaskOutcome.APPROVED.equals(outcome)) {
            Transaction transaction = transactionService.getTransaction(transactionIdentifier);
            publishAcquiringAccountTransactionEvent(transaction, currentUser);
        } else if (TaskOutcome.REJECTED.equals(outcome)) {
            //Also reject other transactions linked with the same task
            finaliseOtherTaskTransactions(taskDetailsDTO.
                getTransactionIdentifiers().
                stream().
                filter(Objects::nonNull).
                filter(t -> !t.equals(transactionIdentifier)).
                toList(),TaskOutcome.REJECTED);
        }

        return TransactionProposalCompleteResponse.transactionProposalCompleteResponseBuilder()
            .requestIdentifier(finaliseResult.getRequestIdentifier())
            .transactionIdentifier(transactionIdentifier)
            .executionTime(finaliseResult.getExecutionTime())
            .executionDate(finaliseResult.getExecutionDate())
            .build();
    }
    
    private void finaliseOtherTaskTransactions(List<String> otherIdentifiers,
        TaskOutcome rejected) {
        for (String identifier:otherIdentifiers) {
            transactionService.finaliseTransaction(identifier, rejected,
                authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN));            
        }
        
    }


    private String getTransactionIdentifier(AllTransactionTaskDetailsDTO taskDetailsDTO) {
        if (taskDetailsDTO.getTransactionIdentifiers().stream().distinct().count() > 1) {
            //NAT and NER case
            return taskDetailsDTO.
                getTransactionIdentifiers().
                stream().
                map(transactionService::getTransaction).
                filter(t -> Objects.nonNull(t.getAttributes())).
                filter(t -> t.getAttributes().contains(AllocationType.NER.toString())).
                map(t -> t.getIdentifier()).
                findFirst().orElseThrow();
        } else {
            return taskDetailsDTO.getTransactionIdentifiers().stream().findFirst().orElseThrow();
        }       
    }

    private void publishAcquiringAccountTransactionEvent(Transaction transaction, User currentUser) {

        String action = TRANSACTION_PROPOSAL_TASK;
        String description;
        String transferringAccountIdentifier = String.valueOf(transaction.getTransferringAccount()
            .getAccountIdentifier());
        String acquiringAccountIdentifier = String.valueOf(transaction.getAcquiringAccount()
            .getAccountIdentifier());
        switch (transaction.getType()) {
            case IssueOfAAUsAndRMUs:
                description = String.format("Transaction identifier %s. Issue AAU or RMU.",
                    transaction.getIdentifier());
                eventService.createAndPublishEvent(acquiringAccountIdentifier,
                    currentUser.getUrid(),
                    description, EventType.ACCOUNT_TASK_COMPLETED, action);
                break;
            case ExternalTransfer:
                break;
            default:
            	 description = String.format("Transaction identifier %s. Transferring account %s",
            		transaction.getIdentifier(), transferringAccountIdentifier);
                eventService.createAndPublishEvent(acquiringAccountIdentifier, currentUser.getUrid(),
                    description, EventType.ACCOUNT_TASK_COMPLETED, action);
        }
    }

    /**
     * Checks if the user trying to view a transaction proposal task details has the appropriate access rights.
     *
     * @param transferringAccountIdentifier the transferring account business identifier.
     * @param acquiringAccountIdentifier    the acquiring account business identifier.
     */
    private void checkTransactionProposalTaskReadAccessRights(Long transferringAccountIdentifier,
                                                              Long acquiringAccountIdentifier) {

        // If the user does not have any permission (READ or COMPLETE) on the transaction proposal task,
        // then he is not allowed to view it.
        if (!authorizationService.getScopes()
            .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_SRA_COMPLETE.getScopeName()) &&
            !authorizationService.getScopes()
                .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE.getScopeName()) &&
            !authorizationService.getScopes()
                .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_READ.getScopeName())) {
            throw TaskActionException.create(TaskActionError.builder().
                code(TaskActionError.NO_PERMISSION_TO_READ_TASK)
                .message(
                    "You do not have the permission to view this task.")
                .build());
        } else if (authorizationService.getScopes()
            .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE.getScopeName()) &&
            !accountAuthorizationService
                .checkAccountAccess(transferringAccountIdentifier, AccountAccessRight.READ_ONLY) &&
            !accountAuthorizationService
                .checkAccountAccess(acquiringAccountIdentifier, AccountAccessRight.READ_ONLY)) {
            // If the user is an authorised representative but not on the transferring account,
            // then he is not allowed to view the task.
            throw TaskActionException.create(TaskActionError.builder().
                code(TaskActionError.NO_PERMISSION_TO_READ_TASK)
                .message(
                    "You do not have the permission to view this task.")
                .build());
        }

    }

    /**
     * Checks if the user performing an action on a Transaction Proposal task, has the required access rights.
     * If not, an exception is thrown.
     *
     * @param outcome               The task outcome.
     * @param initiatorId           The ID of the initiator user.
     * @param transactionIdentifier The business identifier of the task's transaction.
     * @param accountIdentifier     The task's account business identifier.
     * @deprecated implement {@link gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule}
     * and secure at {@link #complete(AllTransactionTaskDetailsDTO, TaskOutcome, String)}
     */
    @Deprecated(forRemoval = true, since = "0.9.0")
    public void checkTransactionProposalTaskCompleteAccessRights(TaskOutcome outcome, Long initiatorId,
                                                                 String transactionIdentifier,
                                                                 String accountIdentifier) {

        Transaction transaction = transactionService.getTransactionWithBlocks(transactionIdentifier);
        Optional<TransactionTypeTaskService> transactionTypeTaskServiceOptional =
            getTaskService(transaction.getType());
        if (transactionTypeTaskServiceOptional.isPresent()) {
            transactionTypeTaskServiceOptional.get().checkForInvalidCompletePermissions();
            return;
        }
        // legacy code

        // If outcome is approve and assignor equals assignee, the 4-eyes principle applies
        if (outcome.equals(TaskOutcome.APPROVED) &&
            userService.getCurrentUser().getId().equals(initiatorId)) {
            throw TaskActionException.create(TaskActionError.builder().
                code(TaskActionError.INITIATOR_NOT_ALLOWED_TO_COMPLETE_TASK)
                .message(
                    "You cannot approve a task initiated by you. The 4-eyes security principle applies to this " +
                        "task.")
                .build());
        }


        // The check for Authorised Representative rights is performed only if the transaction type is not KP Unit
        // Issuance.
        // In case of KP Unit Issuance, the user is definitely a Senior Registry administrator (this is
        // imposed by the IAM).
        // checkAccountAccess throws an exception in case that the user does not have the right permissions.
        if (!TransactionType.IssueOfAAUsAndRMUs.equals(transaction.getType()) &&
            authorizationService.hasScopePermission(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE)) {
            boolean validTransactionForSurrenderAuthorisedRepresentative =
                validTransactionForSurrenderAuthorisedRepresentative(transaction.getType(),
                    Long.parseLong(accountIdentifier), userService.getCurrentUser().getUrid());
            boolean arWithApproveRight = validTransactionForSurrenderAuthorisedRepresentative ||
                accountAuthorizationService
                    .checkAccountAccess(Long.parseLong(accountIdentifier), AccountAccessRight.APPROVE);
            boolean arWithInitiateOrApproveRight = arWithApproveRight || accountAuthorizationService
                .checkAccountAccess(Long.parseLong(accountIdentifier), AccountAccessRight.INITIATE);
            if (TaskOutcome.APPROVED.equals(outcome) && !arWithApproveRight) {
                throw TaskActionException.create(TaskActionError.builder()
                    .code(TaskActionError.NO_PERMISSION_TO_COMPLETE_TASK)
                    .message(
                        "You need to have the approve or the initiate and approve access rights on this account in " +
                            "order to approve a transaction proposal.")
                    .build());
            } else if (TaskOutcome.REJECTED.equals(outcome) && !arWithInitiateOrApproveRight) {
                throw TaskActionException.create(TaskActionError.builder()
                    .code(TaskActionError.NO_PERMISSION_TO_COMPLETE_TASK)
                    .message(
                        "You need to have the approve or the initiate access rights on this account in " +
                            "order to reject a transaction proposal.")
                    .build());
            }

            // An AR cannot complete a task that was initiated by a (senior or junior) registry administrator.
            User initiator = userService.getUserById(initiatorId);
            boolean initiatorIsAnAdmin =
                authorizationService.getClientLevelRoles(initiator.getIamIdentifier()).stream()
                    .anyMatch(roleRepresentation -> UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral()
                        .equals(roleRepresentation.getName()) ||
                        UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral()
                            .equals(roleRepresentation.getName()));
            if (initiatorIsAnAdmin) {
                throw TaskActionException.create(TaskActionError.builder().
                    code(TaskActionError.AR_NOT_ALLOWED_TO_COMPLETE_TASK_INITIATED_BY_ADMIN)
                    .message(
                        "You cannot complete this task because it was initiated by a registry administrator.")
                    .build());
            }
        }
    }

    /**
     * Checks the given tasks for invalid assign permissions.
     *
     * @param assignee the assignee user.
     * @param tasks    the tasks.
     * @deprecated implement rules in {@link #checkForInvalidAssignPermissions()}
     */
    @Protected({
        JuniorAdminCannotBeAssignedToTaskRule.class
    })
    @Transactional
    @Deprecated(forRemoval = true, since = "0.9.0")
    public void checkForInvalidAssignPermissions(User assignee, List<Task> tasks) {


        TaskActionException thrownException = new TaskActionException();

        tasks.stream()
            .filter(task -> appliesFor().contains(task.getType()))
            .forEach(task -> {
            Transaction transaction = transactionService.getTransactionWithBlocks(task.getTransactionIdentifiers().get(0).getTransactionIdentifier());

            Optional<TransactionTypeTaskService> transactionTypeTaskServiceOptional =
                getTaskService(transaction.getType());
            if (transactionTypeTaskServiceOptional.isPresent()) {
                transactionTypeTaskServiceOptional.get().checkForInvalidAssignPermissions();
                return;
            }

            if (
                authorizationService.getScopes()
                    .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_READ.getScopeName())) {
                thrownException.addError(TaskActionError.builder()
                    .code(TaskActionError.ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TASK)
                    .message("Assignor is not allowed to assign tasks of type TRANSACTION_REQUEST "
                        + ". Assignor has READ permission on TRANSACTION_REQUEST type")
                    .build());
            }

            //Check if assignee has SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_SRA_COMPLETE on Task Resource
            PolicyEvaluationRequest policyEvaluationRequest = new PolicyEvaluationRequest();
            policyEvaluationRequest.addResource(Resource.TASK_RESOURCE.getResourceName(),
                Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_SRA_COMPLETE.getScopeName());
            policyEvaluationRequest.setUserId(assignee.getIamIdentifier());
            policyEvaluationRequest.setEntitlements(true);
            PolicyEvaluationResponse.EvaluationResultRepresentation genericSraCompletePermission =
                authorizationService
                    .evaluate(policyEvaluationRequest);

            // The following check is summed-up to the following rules:
            // 1. Check if the assigner has transaction proposal COMPLETE rights.
            // 2. Check if the assignee is a Senior Registry admin.
            // 3. Check if the assignee has APPROVE or INITIATE access rights for the transaction transferring
            // account.
            // If any of the above checks fail, then the task cannot be assigned to the specified user and
            // an exception is thrown.
            boolean assignerRights = authorizationService.getScopes()
                .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_SRA_COMPLETE.getScopeName()) ||
                authorizationService.getScopes()
                    .contains(Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE.getScopeName());
            boolean assigneeIsSra = DecisionEffect.PERMIT.equals(genericSraCompletePermission.getStatus());
            // If the assignee is not a Senior Registry administrator. Therefore, we need to check to see if
            // the assignee is an AR of the account.
            boolean assigneeWithApproveRightForTransferringAccount = accountAuthorizationService
                .checkAccountAccess(task.getAccount().getIdentifier(), assignee.getUrid(),
                    AccountAccessRight.APPROVE);
            boolean assigneeWithInitiateRightForTransferringAccount = accountAuthorizationService
                .checkAccountAccess(task.getAccount().getIdentifier(), assignee.getUrid(),
                    AccountAccessRight.INITIATE);
            boolean validTransactionForSurrenderAuthorisedRepresentative =
                validTransactionForSurrenderAuthorisedRepresentative(transaction.getType(),
                    task.getAccount().getIdentifier(), assignee.getUrid());
            if (assignerRights &&
                !assigneeIsSra &&
                !assigneeWithApproveRightForTransferringAccount &&
                !assigneeWithInitiateRightForTransferringAccount &&
                !validTransactionForSurrenderAuthorisedRepresentative) {
                thrownException.addError(TaskActionError.builder()
                    .code(TaskActionError.ASSIGNEE_NOT_ALLOWED_TO_BE_ASSIGNED_WITH_TASK)
                    .message(String.format("Assignee is not allowed to be assigned the task with request ID %d",
                        task.getRequestId()))
                    .build());
            }

        });
        if (!thrownException.getTaskActionErrors().isEmpty()) {
            throw thrownException;
        }
    }

    private boolean validTransactionForSurrenderAuthorisedRepresentative(TransactionType transactionType,
                                                                         Long accountIdentifier,
                                                                         String assigneeUrid) {

        if (transactionType.isOptionAvailableToSurrenderAR()) {
            return accountAuthorizationService.checkAccountAccess(accountIdentifier, assigneeUrid,
                    AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        }

        return false;
    }

    @Protected({
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
    }

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.TRANSACTION_REQUEST);
    }
}
