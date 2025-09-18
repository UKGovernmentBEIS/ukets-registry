package gov.uk.ets.registry.api.task.service;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationExcelFileGenerator;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.UserStatusEnrolledRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyEnrolledUsersCanCompleteTaskRule;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.model.events.DomainEvent;
import gov.uk.ets.registry.api.common.model.services.OpenSessionInViewAwareLockService;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.common.search.JpaQueryExtractor;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.Assignor;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskAssignment;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.repository.TaskAssignmentRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.shared.EndUserSearch;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalTaskService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jakarta.persistence.LockModeType;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockOptions;
import org.hibernate.cfg.AvailableSettings;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for tasks.
 */
@Log4j2
@Service
public class TaskService {

    public static final String COMMENT_FORMAT = "%s (comment)";
    public static final String COMMENT_TOO_LONG_MESSAGE = "comment too long";
    public static final String COMMENT_REQUIRED_TO_CLAIM_ALREADY_CLAIMED_TASK =
        "Comment is mandatory when claiming claimed tasks.";

    /**
     * AUTHORIZED_REPRESENTATIVE_CRUD_LIST, is an immutable list.
     * It is protected to have package & subclass access, if needed.
     */
    protected static final List<RequestType> AUTHORIZED_REPRESENTATIVE_CRUD_LIST = List.of(
            RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final TaskConversionService taskConversionService;

    private final AuthorizationService authorizationService;

    private final LoadBulkTasksService loadBulkTasksService;

    private final ReportRoleMappingService reportRoleMappingService;

    /**
     * @deprecated TODO: to be deleted - added only where to be able to migrate in phases to the new business rules store
     */
    @Deprecated(forRemoval = true)
    private final TransactionProposalTaskService transactionProposalTaskService;

    private final EventService eventService;

    /**
     * Maps task types with specific task services.
     */
    private final EnumMap<RequestType, TaskTypeService> taskTypeServiceMap = new EnumMap<>(RequestType.class);

    private final UploadedFilesRepository uploadedFilesRepository;

    private final OpenSessionInViewAwareLockService openSessionInViewAwareLockService;

    private final ReportRequestService reportRequestService;

    private final RequestAllocationExcelFileGenerator excelFileGenerator;

    private final TaskAssignmentRepository taskAssignmentRepository;

    /**
     * Constructor.
     *
     * @param taskRepository          the repository for tasks.
     * @param userService             the service for users.
     * @param taskConversionService   convert tasks service
     * @param authorizationService    authorization service
     * @param taskTypeServices        a set of task speficic services.
     * @param uploadedFilesRepository the repository for uploaded files.
     * @param reportRequestService
     * @param excelFileGenerator
     */
    public TaskService(
        TaskRepository taskRepository,
        UserService userService,
        TaskConversionService taskConversionService,
        AuthorizationService authorizationService,
        LoadBulkTasksService loadBulkTasksService,
        ReportRoleMappingService reportRoleMappingService,
        TransactionProposalTaskService transactionProposalTaskService,
        Set<TaskTypeService> taskTypeServices,
        EventService eventService,
        UploadedFilesRepository uploadedFilesRepository,
        OpenSessionInViewAwareLockService openSessionInViewAwareLockService,
        ReportRequestService reportRequestService,
        RequestAllocationExcelFileGenerator excelFileGenerator,
        TaskAssignmentRepository taskAssignmentRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskConversionService = taskConversionService;
        this.authorizationService = authorizationService;
        this.loadBulkTasksService = loadBulkTasksService;
        this.reportRoleMappingService = reportRoleMappingService;
        this.transactionProposalTaskService = transactionProposalTaskService;
        this.eventService = eventService;
        this.uploadedFilesRepository = uploadedFilesRepository;
        this.reportRequestService = reportRequestService;
        registerTaskServices(taskTypeServices);
        this.openSessionInViewAwareLockService = openSessionInViewAwareLockService;
        this.excelFileGenerator = excelFileGenerator;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    /**
     * Registers all task specific services in a map.
     *
     * @param taskTypeServices the task specific services
     */
    private void registerTaskServices(Set<TaskTypeService> taskTypeServices) {
        log.info("Registering task specific services");
        taskTypeServices.forEach(taskTypeService -> {
            Set<RequestType> set = taskTypeService.appliesFor();
            set.forEach(taskType -> {
                if (taskTypeServiceMap.containsKey(taskType)) {
                    throw new UkEtsException(String.format("Task service already registered for:%s", taskType));
                } else {
                    log.info("Registering service for task type:{} -> {}", taskType, taskTypeService);
                    taskTypeServiceMap.put(taskType, taskTypeService);
                }
            });
        });
    }

    /**
     * Select a specific service to perform action for tasks based on task Type.
     *
     * @param requestType the task request type
     * @return
     */
    private Optional<TaskTypeService> getTaskService(RequestType requestType) {
        TaskTypeService taskTypeService = taskTypeServiceMap.get(requestType);
        if (taskTypeService != null) {
            return Optional.of(taskTypeService);
        }
        return Optional.empty();
    }

    /**
     * Sets an event type based on the task status.
     *
     * @param taskStatus the task status.
     * @return an event type.
     */
    public static EventType setEventType(RequestStateEnum taskStatus) {
        EventType eventType;
        if (RequestStateEnum.APPROVED.equals(taskStatus)) {
            eventType = EventType.TASK_APPROVED;
        } else if (RequestStateEnum.REJECTED.equals(taskStatus)) {
            eventType = EventType.TASK_REJECTED;
        } else {
            eventType = EventType.TASK_COMPLETED;
        }
        return eventType;
    }

    /**
     * Searches for tasks based on the provided criteria.
     *
     * @param criteria The search criteria
     * @param pageable The pagination information
     * @return A page with tasks.
     */
	public Page<TaskProjection> search(TaskSearchCriteria criteria, Pageable pageable, boolean isReport,
			String bearerToken) {
		if (!authorizationService.hasScopePermission(Scope.SCOPE_ACTION_NON_ADMIN)
				&& !authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
			throw new AuthorizationDeniedException("Invalid scope.", null);
		}

		final boolean isCurrentUserAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
		EndUserSearch endUserSearch = new EndUserSearch();
		endUserSearch.setAdminSearch(isCurrentUserAdmin);
		endUserSearch.setIamIdentifier(authorizationService.getClaim(OAuth2ClaimNames.SUBJECT));
		criteria.setEndUserSearch(endUserSearch);

		return isReport ? searchAndGenerateReport(criteria, bearerToken, isCurrentUserAdmin) : search(criteria, pageable, isCurrentUserAdmin);

	}

	private Page<TaskProjection> searchAndGenerateReport(TaskSearchCriteria criteria, String bearerToken, boolean isAdmin) {
		if (!isAdmin) {
			JPAQuery<TaskProjection> userQuery = taskRepository.getUserQuery(criteria);
			reportRequestService.requestReport(ReportType.R0026, reportRoleMappingService.getUserReportRequestingRole(),
					JpaQueryExtractor.extractReportQueryInfo(userQuery.createQuery()), bearerToken);
		} else {
			JPAQuery<TaskProjection> adminQuery = taskRepository.getAdminQuery(criteria);
			reportRequestService.requestReport(ReportType.R0005, reportRoleMappingService.getUserReportRequestingRole(),
					JpaQueryExtractor.extractReportQueryInfo(adminQuery.createQuery()), bearerToken);
		}
		return Page.empty();
	}
    
	private Page<TaskProjection> search(TaskSearchCriteria criteria, Pageable pageable, boolean isAdmin) {
		return !isAdmin ? taskRepository.userSearch(criteria, pageable)
				: taskRepository.adminSearch(criteria, pageable);
	}

    /**
     * Updates the task.
     *
     * @param updateInfo       The information to update the task.
     * @param taskUpdateAction The update action type.
     * @param taskDetails      The task details.
     * @return the updated task details dto.
     */
    public TaskDetailsDTO updateTask(String updateInfo, TaskDetailsDTO taskDetails,
                                     TaskUpdateAction taskUpdateAction) {
        if (taskDetails != null) {
            return taskTypeServiceMap.get(taskDetails.getTaskType())
                .updateTask(updateInfo,
                    taskDetails, taskUpdateAction);
        }
        throw new IllegalArgumentException("Task details are missing");
    }

    /**
     * Retrieves a task based on its task ID.
     *
     * @param requestId The unique task business identifier.
     * @return a task.
     * TODO: this is called to get the header, could we call {@link #getTaskDetails}
     * instead and set there the userClaimant
     */
    public TaskDetailsDTO getTaskDetailsDTO(Long requestId) {
        TaskDetailsDTO taskDetailsDTO = taskRepository.getTaskDetails(requestId);
        taskDetailsDTO.setCurrentUserClaimant(
            userService.getCurrentUser().getUrid().equals(taskDetailsDTO.getClaimantURID()));
        taskDetailsDTO.setSubTasks(taskRepository.getSubTaskDetails(taskDetailsDTO.getRequestId()));
        return taskDetailsDTO;
    }

    /**
     * Retrieves a task file by the file id.
     *
     * @param fileId The id of the file
     * @return {@link UploadedFile}
     */
    public UploadedFile getTaskFileById(Long fileId) {
        return uploadedFilesRepository.findById(fileId)
            .orElseThrow(() -> new FileUploadException("Error while fetching the file"));
    }

    /**
     * Retrieves the specific task file
     *
     * @param infoDTO The {@link TaskFileDownloadInfoDTO} info
     * @return {@link UploadedFile}
     */
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        Optional<TaskTypeService> taskServiceOpt = getTaskService(infoDTO.getTaskType());
        if (taskServiceOpt.isPresent()) {
            UploadedFile file = taskServiceOpt.get().getRequestedTaskFile(infoDTO);
            if (RequestType.ALLOCATION_REQUEST.equals(infoDTO.getTaskType())) {
                byte[] extendedExcelFile = excelFileGenerator.generateExtendedExcel(file.getFileData());
                file.setFileData(extendedExcelFile);
            }
            return file;
        } else {
            throw new IllegalCallerException(
                String.format("Request task file not supported for %s", infoDTO.getTaskType()));
        }
    }

    /**
     * Retrieves a task's details based on its task ID.
     *
     * @param requestId The unique task business identifier.
     * @return a task.
     */
    public TaskDetailsDTO getTaskDetails(Long requestId) {
        TaskDetailsDTO taskDetailsDTO = this.getTaskDetailsDTO(requestId);
        if (taskDetailsDTO != null) {
            Optional<TaskTypeService> taskService = getTaskService(taskDetailsDTO.getTaskType());
            if (taskService.isPresent()) {
                return taskService.get().getDetails(taskDetailsDTO);
            } else {
                return taskDetailsDTO;
            }
        }
        return null;
    }

    /**
     * Retrieves the task identifier based on the transaction identifier.
     *
     * @param transactionIdentifier The transaction identifier.
     * @return the task identifier.
     */
    @Transactional
    public Long getTaskIdentifier(String transactionIdentifier) {
        return taskRepository.getTaskIdentifier(transactionIdentifier);
    }

    /**
     * Completes a task.
     *
     * @param requestId The unique business identifier.
     * @param outcome   The outcome (e.g. approve, reject).
     * @param comment   The comment.
     */
    @Protected({
        OnlyEnrolledUsersCanCompleteTaskRule.class
    })
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.TASK_COMPLETE_OUTCOME)
    public TaskCompleteResponse complete(Long requestId, TaskOutcome outcome, String comment) {
        TaskCompleteResponse response = null;
        if (requestId == null) {
            throw new IllegalArgumentException("Input parameter requestId cannot be empty");
        }
        if (outcome == null) {
            throw new IllegalArgumentException("Input parameter outcome cannot be empty");
        }
        Task currentTask = taskRepository.findByRequestId(requestId);
        if (currentTask == null) {
            throw new IllegalArgumentException("Task does not exist");
        }
        try {
            // lock the underlying row with no wait option.
            // There is no need to block the requests at this point but we want to make them fail fast
            // if another request is currently completing the same task.
            openSessionInViewAwareLockService.lock(currentTask, LockModeType.PESSIMISTIC_WRITE,
                Collections.singletonMap(AvailableSettings.JPA_LOCK_TIMEOUT, LockOptions.NO_WAIT));
        } catch (Throwable throwable) {
            throw new IllegalStateException("Other task completion request is in process", throwable);
        }
        if (Set.of(RequestStateEnum.APPROVED, RequestStateEnum.REJECTED).contains(currentTask.getStatus())) {
            throw new IllegalArgumentException("The task is already completed");
        }
        TaskDetailsDTO taskDetailsDTO = getTaskDetails(requestId);
        if (taskDetailsDTO == null) {
            throw new IllegalArgumentException(String
                .format("Task with unique business identifier %d not found in the database",
                    requestId));
        }
        // we need the completedDate here to generate the compliance event accordingly
        taskDetailsDTO.setCompletedDate(new Date());

        Optional<TaskTypeService> taskTypeServiceOptional = this.getTaskService(taskDetailsDTO.getTaskType());
        if (taskTypeServiceOptional.isPresent()) {
            TaskTypeService taskTypeService = taskTypeServiceOptional.get();
            response = taskTypeService.complete(taskDetailsDTO, outcome, comment);
        }
        if (response == null) {
            response = TaskCompleteResponse.builder().requestIdentifier(requestId).build();
        }
        complete(taskDetailsDTO, outcome, comment);
        response.setTaskDetailsDTO(this.getTaskDetails(requestId));
        //TODO get mails from response and send them here
        return response;
    }

    /**
     * Completes a task and logs the completion event.
     *
     * @param task    The completed task
     * @param outcome The task completion outcome
     * @param comment The comment regarding the task completion.
     */
    private void complete(TaskDetailsDTO task, TaskOutcome outcome, String comment) {
        User currentUser = userService.getCurrentUser();
        final RequestStateEnum taskStatus = taskConversionService.getTaskStatus(outcome);
        Task byRequestId = taskRepository.findByRequestId(task.getRequestId());
        byRequestId.setCompletedBy(currentUser);
        byRequestId.setStatus(taskStatus);
        byRequestId.setCompletedDate(task.getCompletedDate());

        String action = String
            .format("%s", byRequestId.generateEventTypeDescription(getCompletedVerb(outcome, byRequestId.getType())));
        eventService
            .createAndPublishEvent(task.getRequestId().toString(), currentUser.getUrid(), "", setEventType(taskStatus),
                action);
        if (comment != null) {
            eventService.createAndPublishEvent(task.getRequestId().toString(), currentUser.getUrid(), comment,
                EventType.TASK_COMMENT,
                String.format(COMMENT_FORMAT, action));
        }

        if (task.getAccountNumber() != null) {
           if((AUTHORIZED_REPRESENTATIVE_CRUD_LIST.contains(task.getTaskType()))&&
                   (task.getRequestStatus().equals(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED))){
                   comment = (comment == null)
                           ? task.getAccountFullIdentifier()
                           : comment.concat("\n").concat(task.getAccountFullIdentifier());
            }
            eventService.createAndPublishEvent(task.getAccountNumber(), currentUser.getUrid(),
                String.format("Task requestId %s %s", task.getRequestId(), outcome.name()),
                EventType.ACCOUNT_TASK_COMPLETED, action);
            // the comment, is under condition since it may still be null.
            if (comment != null) {
                eventService.createAndPublishEvent(task.getAccountNumber(), currentUser.getUrid(), comment,
                    EventType.ACCOUNT_TASK_COMMENT, String.format(COMMENT_FORMAT, action));
            }
        }

        // if the task is associated with a user (initiator id) and also completed by a different user
        // we need to show an event in user history:
        if (task.getInitiatorUrid() != null && !task.getInitiatorUrid().equals(currentUser.getUrid())) {
            //Initiator may not be the affected user
        	if(RequestType.USER_DETAILS_UPDATE_REQUEST.equals(byRequestId.getType())) {
        		comment = Utils.concat(", ", comment, UserDetailsUtil.generateUserDetailsUpdateComment(((UserDetailsUpdateTaskDetailsDTO)task).getCurrent(), ((UserDetailsUpdateTaskDetailsDTO)task).getChanged()));
        	}
            eventService
                .createAndPublishEvent(task.getReferredUserURID(), currentUser.getUrid(), StringUtils.defaultString(comment),
                    EventType.USER_TASK_COMPLETED,
                    action);
        }
    }

    /**
     * Returns the appropriate verb to depict a completed task.
     *
     * @param outcome The task outcome.
     * @return a string.
     */
    private String getCompletedVerb(TaskOutcome outcome, RequestType type) {
        String result = " completed.";

        if (Set.of(RequestType.CHANGE_TOKEN, RequestType.LOST_TOKEN, RequestType.REQUESTED_EMAIL_CHANGE,
                RequestType.LOST_PASSWORD_AND_TOKEN)
            .contains(type)) {
            result = TaskOutcome.APPROVED.equals(outcome) ? " approved." : " rejected.";
        }
        return result;
    }

    /**
     * Comments on the task of requestId.
     *
     * @param requestId The unique business identifier of task
     * @param comment   The comment.
     */
    @Transactional
    public void comment(Long requestId, String comment) {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId should not be null");
        }
        if (comment == null) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_REQUIRED)
                .message("Comment is required")
                .build());
        }

        if (comment.length() > Constants.COMMENT_MAX_LENGTH) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_TOO_LONG)
                .message(COMMENT_TOO_LONG_MESSAGE)
                .build());
        }

        Task task = Optional.ofNullable(taskRepository.findByRequestId(requestId)).orElseThrow(() ->
            TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.TASK_NOT_FOUND)
                .message("task not found")
                .build()));

        if (task.isCompleted()) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.TASK_COMPLETED)
                .message(
                    "task of request id : " + requestId + " is completed and cannot be commented")
                .build());
        }

        User commentator = userService.getCurrentUser();

        if (!commentator.equals(task.getClaimedBy())) {
            throw TaskActionException.create(TaskActionError.builder().
                code(TaskActionError.USER_SHOULD_BE_THE_CLAIMANT)
                .message("user of urid " + commentator.getUrid()
                    + " tried to comment on a task which is not claimed by him")
                .build());
        }
        String action = "Task (comment)";
        eventService.createAndPublishEvent(task.getRequestId().toString(), commentator.getUrid(), comment,
            EventType.TASK_COMMENT, action);
    }

    /**
     * Claims to current user the tasks that correspond to the request ids.
     *
     * @param requestIds The list od request ids
     * @param comment    The comment provided by the user
     */
    @Protected({
        UserStatusEnrolledRule.class,
        RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule.class
    })
    @Transactional
    public void claim(List<Long> requestIds, String comment) {
        if (comment != null && comment.length() > Constants.COMMENT_MAX_LENGTH) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_TOO_LONG)
                .message(COMMENT_TOO_LONG_MESSAGE)
                .build());
        }
        final User claimant = userService.getCurrentUser();
        List<Task> tasksSelected = loadBulkTasksService.loadSelectedTasks(requestIds);
        if (tasksSelected.stream().anyMatch(t -> t.getClaimedBy() != null) && comment == null) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.CLAIM_CLAIMED_WITHOUT_COMMENT)
                .message(COMMENT_REQUIRED_TO_CLAIM_ALREADY_CLAIMED_TASK)
                .build());
        }
        process(tasksSelected, this::checkForInvalidClaimantPermissions, task -> this.claim(task, claimant));
        tasksSelected.forEach(task -> {
            String action = task.generateEventTypeDescription(" claimed.");
            String description = String.format(" Task requestId %s.", task.getRequestId());
            if (task.getAccount() != null) {
                eventService.createAndPublishEvent(task.getAccount().getIdentifier().toString(), claimant.getUrid(),
                    description,
                    EventType.ACCOUNT_TASK_CLAIMED, action);
            }
            if (comment != null) {
                if (task.getAccount() != null) {
                    eventService.createAndPublishEvent(task.getAccount().getIdentifier().toString(), claimant.getUrid(),
                        comment, EventType.ACCOUNT_TASK_COMMENT, String.format(COMMENT_FORMAT, action));
                }
                eventService.createAndPublishEvent(task.getRequestId().toString(), claimant.getUrid(), comment,
                    EventType.TASK_COMMENT, String.format(COMMENT_FORMAT, action));
            }
        });
    }

    /**
     * Assigns tasks to other user. The tasks are the corresponded to the request ids tasks.
     *
     * @param requestIds The request ids of tasks
     * @param urid       The urid of the user that the tasks are going to be claimed to.
     * @param comment    The comment provided by the user
     */
    @Protected({
        UserStatusEnrolledRule.class
    })
    @Transactional
    public void assign(List<Long> requestIds, String urid, String comment) {
        if (comment == null) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_REQUIRED)
                .message("Comment is mandatory")
                .build());
        }
        if (comment.length() > Constants.COMMENT_MAX_LENGTH) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_TOO_LONG)
                .message(COMMENT_TOO_LONG_MESSAGE)
                .build());
        }

        Assignor assignor = new Assignor();
        assignor.setScopes(authorizationService.getScopes());
        assignor.setUser(userService.getCurrentUser());
        final User assignee = Optional.ofNullable(userService.getUserByUrid(urid)).orElseThrow(() ->
            TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.USER_NOT_FOUND)
                .urid(urid)
                .message("User with urid " + urid + " does not exist")
                .build())
        );
        if (UserStatus.DEACTIVATED.equals(assignee.getState()) ||
            UserStatus.DEACTIVATION_PENDING.equals(assignee.getState())) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.ASSIGNEE_NOT_ALLOWED_TO_BE_ASSIGNED_WITH_TASK)
                .urid(urid)
                .message("User with urid " + urid + " is not allowed to be assigned with task")
                .build());
        }

        List<Task> tasksSelected = loadBulkTasksService.loadSelectedTasks(requestIds);
        process(tasksSelected,
            tasks -> this.checkForAssignmentPermissionsError(assignee, tasks),
            task -> this.assign(task, assignor, assignee, comment));
        tasksSelected.forEach(task -> {
            String action = task.generateEventTypeDescription(" assigned.");
            eventService.createAndPublishEvent(task.getRequestId().toString(), assignor.getUser().getUrid(), comment,
                EventType.TASK_COMMENT, String.format(COMMENT_FORMAT, action));
            if (task.getAccount() != null) {
                String description = String.format("Task with requestId %s assigned to %s", task.getRequestId(),
                    assignee.getDisclosedName());
                String accountIdentifier = task.getAccount().getIdentifier().toString();
                eventService.createAndPublishEvent(accountIdentifier, assignor.getUser().getUrid(), description,
                    EventType.ACCOUNT_TASK_ASSIGNED, action);
                eventService.createAndPublishEvent(accountIdentifier, assignor.getUser().getUrid(), comment,
                    EventType.ACCOUNT_TASK_COMMENT, String.format(COMMENT_FORMAT, action));
            }
        });
    }

    private void process(List<Task> tasks, Consumer<List<Task>> checkPermissionsOnTaskType,
                         Function<Task, DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent>> action) {
        checkPermissionsOnTaskType.accept(tasks);
        var events = tasks.stream().map(action).collect(Collectors.toList());
        checkForDomainErrors(events);
        events.stream().map(DomainEvent::getPayload).forEach(event -> eventService.publishEvent(event));
    }

    private void checkForDomainErrors(List<DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent>> events) {
        Predicate<DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent>> invalidDomainEvent =
            event -> !event.isValid();
        if (events.stream().noneMatch(invalidDomainEvent)) {
            return;
        }
        final TaskActionException exception = new TaskActionException();
        events.stream().filter(invalidDomainEvent).forEach(event -> {
            final Long requestId = Long.valueOf(event.getPayload().getDomainObject().domainId());
            event.getErrors().forEach(err ->
                exception.addError(TaskActionError.builder()
                    .code(err.getCode())
                    .requestId(requestId)
                    .message(err.getMessage())
                    .build()));
        });

        throw exception;
    }

    private void checkForInvalidClaimantPermissions(List<Task> tasks) {
        Set<RequestType> requestTypes = loadBulkTasksService.getRequestType(tasks);

        for (RequestType requestType : requestTypes) {
            getTaskService(requestType).ifPresent(TaskTypeService::checkForInvalidClaimantPermissions);
        }
    }

    private DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> claim(Task task, User claimant) {
        getTaskService(task.getType()).ifPresent(taskTypeService -> taskTypeService.claim(task));
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> event = task.claim(claimant);
        addTaskAssignment(task);
        return event;
    }

    private void checkForAssignmentPermissionsError(User assignee, List<Task> tasks) {
        Set<RequestType> requestTypes = loadBulkTasksService.getRequestType(tasks);

        if (requestTypes.contains(RequestType.TRANSACTION_REQUEST)) {
            // TODO refactor this when the checkForInvalidClaimantPermissions is removed
            transactionProposalTaskService.checkForInvalidAssignPermissions(assignee, tasks);
        }

        for (RequestType requestType : requestTypes) {
            getTaskService(requestType).ifPresent(TaskTypeService::checkForInvalidAssignPermissions);
        }
    }

    private DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> assign(Task task, Assignor assignor,
                                                                               User assignee, String comment) {
        getTaskService(task.getType()).ifPresent(taskTypeService -> taskTypeService.assign(task));
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> event = task.assign(assignor, assignee, comment);
        addTaskAssignment(task);
        return event;
    }

    private void addTaskAssignment(Task task) {

        // Valid roles: Admins/Authority/AuthorizedRepresentative
        List<String> acceptedRoles = new ArrayList<>(UserRole.getRolesForRoleBasedAccess());
        acceptedRoles.add(UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());

        String roles = Optional.of(task.getClaimedBy())
            .map(User::getUserRoles)
            .stream()
            .flatMap(Collection::stream)
            .map(UserRoleMapping::getRole)
            .map(IamUserRole::getRoleName)
            .filter(acceptedRoles::contains)
            .collect(Collectors.joining(","));

        TaskAssignment taskAssignment = TaskAssignment.builder()
            .task(task)
            .urid(task.getClaimedBy().getUrid())
            .assignmentDate(task.getClaimedDate())
            .roles(roles)
            .build();
        Optional.ofNullable(task.getTaskAssignments())
            .ifPresentOrElse(assignments -> assignments.add(taskAssignment),
                () -> task.setTaskAssignments(List.of(taskAssignment)));

        taskAssignmentRepository.save(taskAssignment);
    }
}
