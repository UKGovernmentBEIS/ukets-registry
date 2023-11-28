package gov.uk.ets.registry.api.task.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationExcelFileGenerator;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.model.events.DomainEvent;
import gov.uk.ets.registry.api.common.model.services.OpenSessionInViewAwareLockService;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskAssignment;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskAssignmentRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalTaskService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

public class TaskServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    private TaskService taskService;

    private TaskConversionService taskConversionService;

    @Mock
    private AuthorizationServiceImpl authService;

    LoadBulkTasksService loadBulkTasksService;

    @Mock
    TransactionProposalTaskService transactionProposalTaskService;

    @Mock
    private Set<TaskTypeService> taskTypeServiceSet;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Captor
    protected ArgumentCaptor<Object> publishEventCaptor;

    private User currentUser;
    private User claimant;

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;

    @Mock
    private OpenSessionInViewAwareLockService openSessionInViewAwareLockService;

    @Mock
    private DomainEventEntityRepository domainEventEntityRepository;
    
    @Mock
    private ReportRoleMappingService reportRoleMappingService;

    @Mock
    ReportRequestService reportRequestService;

    @Mock
    RequestAllocationExcelFileGenerator excelFileGenerator;

    @Mock
    TaskAssignmentRepository taskAssignmentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        currentUser = mock(User.class);
        claimant = new User();
        loadBulkTasksService = new LoadBulkTasksService(taskRepository);
        authService = mock(AuthorizationServiceImpl.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        taskConversionService = new TaskConversionService();
        EventService eventService =
            new EventService(applicationEventPublisher, domainEventEntityRepository);

        taskService = new TaskService(taskRepository, userService, taskConversionService,
            authService, loadBulkTasksService, reportRoleMappingService, transactionProposalTaskService, 
            taskTypeServiceSet, eventService, uploadedFilesRepository, openSessionInViewAwareLockService, 
            reportRequestService, excelFileGenerator, taskAssignmentRepository);
    }

    @Test
    public void on_comment_When_comment_is_null_should_throw_TaskActionException() {
        Assertions.assertThrows(Exception.class, () -> testComment(TestCommentCommand.builder()
            .claimant(true)
            .completed(false)
            .requestId(1234L)
            .expectedException(TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.COMMENT_REQUIRED)
                .build()))
            .build()));
    }

    @Test
    public void on_comment_When_the_task_of_requestId_does_not_exist_on_db_should_throw_TaskActionException() {
        Assertions.assertThrows(Exception.class, () -> testComment(TestCommentCommand.builder()
            .claimant(true)
            .completed(false)
            .comment("a comment")
            .requestId(1234L)
            .checkTaskNotFound(true)
            .expectedException(TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.TASK_NOT_FOUND)
                .build()))
            .build()));
    }

    @Test
    public void on_comment_When_commentator_isNot_the_task_claimant_should_throw_TaskActionException() {
        Assertions.assertThrows(Exception.class, () -> testComment(TestCommentCommand.builder()
            .claimant(false)
            .comment("a comment")
            .completed(false)
            .requestId(1234L)
            .expectedException(TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.USER_SHOULD_BE_THE_CLAIMANT)
                .build()))
            .build()));
    }

    @Test
    public void on_comment_when_task_is_completed_should_throw_TaskActionException() {
        Assertions.assertThrows(Exception.class, () -> testComment(TestCommentCommand.builder()
            .claimant(true)
            .comment("a comment")
            .completed(true)
            .requestId(1234L)
            .expectedException(TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.TASK_COMPLETED)
                .build()))
            .build()));
    }

    @Test
    public void on_comment_success_an_event_of_type_of_COMMENT_should_be_created_and_persisted() {
        TestCommentCommand command = TestCommentCommand.builder()
            .claimant(true)
            .comment("a comment")
            .completed(false)
            .requestId(1234L)
            .build();
        Task task = testComment(command);
        verify(applicationEventPublisher, times(1)).publishEvent(publishEventCaptor.capture());
        List<Object> capturedEvents = publishEventCaptor.getAllValues();
        assertEquals(1, capturedEvents.size());
        gov.uk.ets.registry.api.auditevent.DomainEvent domainEvent =
            (gov.uk.ets.registry.api.auditevent.DomainEvent) capturedEvents.get(0);
        assertEquals("Task (comment)", domainEvent.getWhat());
        assertEquals(domainEvent.getDescription(), command.comment);
        assertEquals(task.isCompleted(), command.completed);
    }

    @Test
    @Disabled("to be moved to a test for {@link gov.uk.ets.registry.api.authz.ruleengine.rules.ClaimantCanCompleteTask}")
    public void on_complete_not_by_claimant_an_exception_should_be_thrown() {
        TaskActionError expectedError = TaskActionError.builder()
            .code(TaskActionError.USER_SHOULD_BE_THE_CLAIMANT_TO_COMPLETE)
            .message("You cannot complete this task as you are not the claimant of this task.")
            .build();

        /* Test APPROVE task by non-claimant */
        TestCompleteCommand command = TestCompleteCommand.builder()
            .notClaimant(true)
            .requestId(1234L)
            .outcome(TaskOutcome.APPROVED)
            .comment("No comment")
            .build();
        testComplete(command);

        TaskActionException exception = assertThrows(TaskActionException.class,
            () -> taskService.complete(command.requestId, command.outcome, command.comment));

        assertEquals(exception.getTaskActionErrors().get(0), expectedError);

        /* Test REJECT task by non-claimant */
        command.outcome = TaskOutcome.REJECTED;
        testComplete(command);

        exception = assertThrows(TaskActionException.class,
            () -> taskService.complete(command.requestId, command.outcome, command.comment));

        assertEquals(exception.getTaskActionErrors().get(0), expectedError);
    }

    @Test
    @Disabled("should be moved to account opening task service")
    public void on_complete_account_opening_not_by_user_with_complete_right() {
        TaskActionError expectedError = TaskActionError.builder()
            .code(TaskActionError.NO_PERMISSION_TO_COMPLETE_TASK)
            .message("You do not have permission to complete this task.")
            .build();

        /* Test APPROVE task by user with no complete right */
        TestCompleteCommand command = TestCompleteCommand.builder()
            .notClaimant(false)
            .notPermitted(true)
            .requestType(RequestType.ACCOUNT_OPENING_REQUEST)
            .requestId(1234L)
            .outcome(TaskOutcome.APPROVED)
            .comment("No comment")
            .build();
        testComplete(command);

        TaskActionException exception = assertThrows(TaskActionException.class,
            () -> taskService.complete(command.requestId, command.outcome, command.comment));

        assertEquals(exception.getTaskActionErrors().get(0), expectedError);

        /* Test REJECT task by user with no complete right */
        command.outcome = TaskOutcome.REJECTED;
        testComplete(command);

        exception = assertThrows(TaskActionException.class,
            () -> taskService.complete(command.requestId, command.outcome, command.comment));

        assertEquals(exception.getTaskActionErrors().get(0), expectedError);
    }

    private Task testComment(TestCommentCommand command) {
        if (command.expectedException != null) {
            thrown.expect(is(command.expectedException));
        }
        User commentator = mock(User.class);
        Mockito.when(commentator.getUrid()).thenReturn("commentator");
        Mockito.when(userService.getCurrentUser()).thenReturn(commentator);
        User claimant = mock(User.class);
        Mockito.when(claimant.getUrid()).thenReturn("claimant");

        Task task = mock(Task.class);
        Mockito.when(taskRepository.findByRequestId(command.requestId))
            .thenReturn(command.checkTaskNotFound ? null : task);
        Mockito.when(task.isCompleted()).thenReturn(command.completed);
        Mockito.when(task.getClaimedBy()).thenReturn(command.claimant ? commentator : claimant);

        taskService.comment(command.requestId, command.comment);

        return task;
    }

    private void testComplete(TestCompleteCommand command) {
        TaskDetailsDTO task = mock(TaskDetailsDTO.class);
        Mockito.when(task.getTaskType()).thenReturn(command.requestType);
        Mockito.when(currentUser.getUrid()).thenReturn("UK977538690871");
        if (command.notClaimant) {
            Mockito.when(task.getClaimantURID()).thenReturn("claimant");
        } else if (command.notPermitted) {
            Mockito.when(task.getClaimantURID()).thenReturn("UK977538690871");
            Mockito.when(authService.hasScopePermission(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE))
                .thenReturn(false);
        }
        Mockito.when(taskRepository.getTaskDetails(command.requestId)).thenReturn(task);
    }


    @Test
    public void claim_or_assign_empty_list_of_request_ids_should_throw_TaskBulkException() {
        String urid = "a-user";
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));

        TestBulkActionCommand testCommand = TestBulkActionCommand
            .builder()
            .urid(urid)
            .comment("a comment")
            .requestIds(new ArrayList<>())
            .expectedException(createExpectedException(TaskActionError.NO_TASKS_SELECTED))
            .build();

        verifyClaimFailure(testCommand);
        verifyAssignFailure(testCommand);
    }

    @Test
    public void claim_or_assign_non_existed_request_ids_should_throw_TaskBulkActionException() {
        String urid = "a-user";
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        List<Long> requestIds = Arrays.asList(1L, 2L, 3L, 4L);
        List<Task> tasksOfRequestIdsInDatabase =
            Stream.of(1L, 3L).map(this::mockValidDomainEventTask).collect(Collectors.toList());
        TaskActionException expectedException = createExpectedException(TaskActionError.TASK_NOT_FOUND, 2L, 4L);

        TestBulkActionCommand testCommand = TestBulkActionCommand
            .builder()
            .urid(urid)
            .comment("a comment")
            .requestIds(requestIds)
            .requestIdsTasksResult(tasksOfRequestIdsInDatabase)
            .expectedException(expectedException)
            .build();

        verifyClaimFailure(testCommand);
        verifyAssignFailure(testCommand);
    }

    @Test
    public void assign_task_without_comment_should_throw_TaskBulkActionException() {
        String urid = "a-user";
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        List<Long> requestIds = Arrays.asList(1L, 2L, 3L);
        List<Task> tasksOfMultipleTypes = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());

        TaskActionException expectedException = createExpectedException(TaskActionError.COMMENT_REQUIRED);

        TestBulkActionCommand testCommand =
            TestBulkActionCommand.builder()
                .urid(urid)
                .requestIds(requestIds)
                .requestIdsTasksResult(tasksOfMultipleTypes)
                .expectedException(expectedException)
                .build();

        verifyAssignFailure(testCommand);
    }

    @Test
    public void claim_or_assign_tasks_which_return_invalid_domain_events_should_throw_the_corresponded_to_domain_errors_TaskBulkActionException() {
        final String domainErrorCodePrefix = "test-domain-error-";
        List<Long> requestIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        List<Task> tasks = requestIds.stream().map(id -> id % 2 == 0
            ? mockValidDomainEventTask(id) : mockInvalidDomainEventTask(id,
            domainErrorCodePrefix + id)).collect(Collectors.toList());

        tasks.forEach(task -> Mockito.when(task.getClaimedBy()).thenReturn(claimant));

        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);
        final TaskActionException expectedException = new TaskActionException();
        requestIds.stream().filter(id -> id % 2 != 0).forEach(
            id -> expectedException.addError(TaskActionError
                .builder()
                .code(domainErrorCodePrefix + id)
                .requestId(id).build())
        );

        mockPermissionsForClaimSuccess();
        verifyClaimFailure(TestBulkActionCommand.builder()
            .requestIds(requestIds)
            .requestIdsTasksResult(tasks)
            .expectedException(expectedException)
            .build());

        mockPermissionsForAssignSuccess();
        String urid = "a-user";
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));

        verifyAssignFailure(TestBulkActionCommand.builder()
            .requestIds(requestIds)
            .comment("comment is mandatory")
            .urid(urid)
            .requestIdsTasksResult(tasks)
            .expectedException(expectedException)
            .build());
    }

    @Test
    public void comment_is_not_mandatory_for_claim() {
        String comment = null;
        List<Long> requestIds = Arrays.asList(1L, 2L, 3L, 4L);
        List<Task> tasksOfRequestIdsInDatabase = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasksOfRequestIdsInDatabase);
        Mockito.when(authService.getScopes()).thenReturn(
            Stream.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName())
                .collect(Collectors.toSet()));

        tasksOfRequestIdsInDatabase.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn("");
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(null).thenReturn(claimant);
        });
        taskService.claim(requestIds, comment);

        Mockito.verify(applicationEventPublisher, Mockito.times(8))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(4))
            .save(any(TaskAssignment.class));
    }

    @Test
    public void comment_is_mandatory_when_claiming_claimed_tasks() {
        // given
        List<Long> requestIds = Arrays.asList(100L);
        Task task = mockValidDomainEventTask(requestIds.get(0));
        User currentClaimant = mock(User.class);
        given(task.getClaimedBy()).willReturn(currentClaimant);
        given(taskRepository.findAllByRequestIdIn(requestIds)).willReturn(Arrays.asList(task));
        given(authService.getScopes()).willReturn(
            Stream.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName())
                .collect(Collectors.toSet()));

        TaskActionException exception =
            assertThrows(TaskActionException.class, () -> taskService.claim(requestIds, null));
        assertEquals(exception.getTaskActionErrors().get(0).getCode(), TaskActionError.CLAIM_CLAIMED_WITHOUT_COMMENT);

        assertDoesNotThrow(() -> taskService.claim(requestIds, "claim a claimed task with comment"));
    }

    @Test
    public void claim_task_of_account_opening_type_with_permission_complete_should_succeed() {
        List<Long> requestIds = Collections.singletonList(1L);
        List<Task> tasks = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());

        Mockito.when(userService.getCurrentUser()).thenReturn(mock(User.class));
        Mockito.when(authService.getScopes())
            .thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);

        tasks.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn("");
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(claimant);
        });

        taskService.claim(requestIds, "A comment");

        Mockito.verify(applicationEventPublisher, Mockito.times(4))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(1))
            .save(any(TaskAssignment.class));
    }

    @Test
    public void assign_task_of_account_opening_type_with_permission_complete_should_succeed() {
        String urid = "a-user";
        List<Long> requestIds = Collections.singletonList(1L);
        List<Task> tasks = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        Mockito.when(userService.getCurrentUser()).thenReturn(mock(User.class));
        Mockito.when(authService.getScopes())
            .thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        RoleRepresentation role = new RoleRepresentation("a-role", null, false);
        Mockito.when(authService.getClientLevelRoles(any())).thenReturn(List.of(role));
        Mockito.when(authService.getToken()).thenReturn(mock(AccessToken.class));
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);

        tasks.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn(urid);
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(claimant);
        });

        taskService.assign(requestIds, urid, "A comment");

        Mockito.verify(applicationEventPublisher, Mockito.times(4))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(1))
            .save(any(TaskAssignment.class));
    }

    @Test
    public void assign_task_of_account_opening_type_with_permission_write_should_succeed() {
        String urid = "a-user";
        List<Long> requestIds = Collections.singletonList(1L);
        List<Task> tasks = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        Mockito.when(userService.getCurrentUser()).thenReturn(mock(User.class));
        Mockito.when(authService.getScopes())
            .thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_WRITE.getScopeName()));
        RoleRepresentation role = new RoleRepresentation("a-role", null, false);
        Mockito.when(authService.getClientLevelRoles(any())).thenReturn(List.of(role));
        Mockito.when(authService.getToken()).thenReturn(mock(AccessToken.class));
        PolicyEvaluationResponse.EvaluationResultRepresentation
            result = new PolicyEvaluationResponse.EvaluationResultRepresentation();
        result.setStatus(DecisionEffect.DENY);
        Mockito.when(authService.evaluate(any())).thenReturn(result);
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);

        tasks.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn(urid);
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(claimant);
        });

        taskService.assign(requestIds, urid, "A comment");

        Mockito.verify(applicationEventPublisher, Mockito.times(4))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(1))
            .save(any(TaskAssignment.class));
    }

    @Test
    public void assign_task_of_account_opening_type_from_permission_write_to_permission_write_should_succeed_only_if_assignor_is_the_claimant_of_tasks() {
        String urid = "a-user";
        List<Long> requestIds = Collections.singletonList(1L);
        List<Task> tasks = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        Mockito.when(userService.getCurrentUser()).thenReturn(mock(User.class));
        Mockito.when(authService.getScopes())
            .thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_WRITE.getScopeName()));
        RoleRepresentation role = new RoleRepresentation("a-role", null, false);
        Mockito.when(authService.getClientLevelRoles(any())).thenReturn(List.of(role));
        Mockito.when(authService.getToken()).thenReturn(mock(AccessToken.class));
        PolicyEvaluationResponse.EvaluationResultRepresentation
            result = new PolicyEvaluationResponse.EvaluationResultRepresentation();
        result.setStatus(DecisionEffect.DENY);
        Mockito.when(authService.evaluate(any())).thenReturn(result);
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);

        tasks.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn(urid);
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(claimant);
        });
        taskService.assign(requestIds, urid, "A comment");

        Mockito.verify(applicationEventPublisher, Mockito.times(4))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(1))
            .save(any(TaskAssignment.class));
    }

    @Test
    public void assign_task_of_account_opening_type_from_permission_complete_to_permission_complete_should_succeed() {
        String urid = "a-user";
        List<Long> requestIds = Collections.singletonList(1L);
        List<Task> tasks = requestIds.stream()
            .map(this::mockValidDomainEventTask).collect(Collectors.toList());
        Mockito.when(userService.getUserByUrid(urid)).thenReturn(mock(User.class));
        Mockito.when(userService.getCurrentUser()).thenReturn(mock(User.class));
        Mockito.when(authService.getScopes())
            .thenReturn(Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()));
        RoleRepresentation role = new RoleRepresentation("a-role", null, false);
        Mockito.when(authService.getClientLevelRoles(any())).thenReturn(List.of(role));
        Mockito.when(authService.getToken()).thenReturn(mock(AccessToken.class));
        PolicyEvaluationResponse.EvaluationResultRepresentation
            result = new PolicyEvaluationResponse.EvaluationResultRepresentation();
        result.setStatus(DecisionEffect.DENY);
        Mockito.when(authService.evaluate(any())).thenReturn(result);
        Mockito.when(taskRepository.findAllByRequestIdIn(requestIds)).thenReturn(tasks);
        tasks.forEach(task -> {
            TaskDetailsDTO taskDetailsDTO = mock(TaskDetailsDTO.class);
            Mockito.when(taskDetailsDTO.getTaskType()).thenReturn(RequestType.ACCOUNT_OPENING_REQUEST);
            Mockito.when(userService.getCurrentUser().getUrid()).thenReturn(urid);
            Mockito.when(taskRepository.getTaskDetails(task.getRequestId())).thenReturn(taskDetailsDTO);
            Mockito.when(task.getClaimedBy()).thenReturn(claimant);
        });

        taskService.assign(requestIds, urid, "A comment");

        Mockito.verify(applicationEventPublisher, Mockito.times(4))
            .publishEvent(publishEventCaptor.capture());
        Mockito.verify(taskAssignmentRepository, Mockito.times(1))
            .save(any(TaskAssignment.class));
    }


    @Test
    void test_getTaskFileById() {
        Long fileId = 1L;
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        uploadedFile.setFileName("Test Filename");

        Mockito.when(uploadedFilesRepository.findById(fileId)).thenReturn(Optional.of(uploadedFile));

        assertEquals(fileId, uploadedFile.getId());
    }

    private void verifyClaimFailure(TestBulkActionCommand command) {
        command.action = () -> taskService.claim(command.requestIds, command.comment);
        verifyBulkActionFailure(command);
    }

    private void verifyAssignFailure(TestBulkActionCommand command) {
        command.action = () -> taskService.assign(command.requestIds, command.urid, command.comment);
        verifyBulkActionFailure(command);
    }

    private void verifyBulkActionFailure(TestBulkActionCommand command) {
        Exception exception = null;
        Mockito.when(taskRepository.findAllByRequestIdIn(command.requestIds)).thenReturn(command.requestIdsTasksResult);
        try {
            command.action.run();
        } catch (Exception e) {
            exception = e;
        }
        assertEquals(command.expectedException, exception);
        verify(applicationEventPublisher, Mockito.times(0))
            .publishEvent(publishEventCaptor.capture());
    }

    private Task mockValidDomainEventTask(Long requestId) {
        Task task = mockTask(requestId, RequestType.ACCOUNT_OPENING_REQUEST);
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> validEvent = new DomainEvent<>();
        gov.uk.ets.registry.api.auditevent.DomainEvent event = mockEvent(task);
        validEvent.setPayload(event);
        Account account = new Account();
        account.setIdentifier(100020L);
        Mockito.when(task.getAccount()).thenReturn(account);
        Mockito.when(task.claim(any())).thenReturn(validEvent);
        Mockito.when(task.assign(any(), any(), any())).thenReturn(validEvent);
        return task;
    }

    private Task mockInvalidDomainEventTask(Long requestId, String domainErrorCode) {
        Task task = mockTask(requestId, RequestType.ACCOUNT_OPENING_REQUEST);
        gov.uk.ets.registry.api.auditevent.DomainEvent event = mockEvent(task);
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> invalidEvent = new DomainEvent<>();
        invalidEvent.setPayload(event);
        invalidEvent.error(domainErrorCode, "test error message of " + requestId);
        Mockito.when(task.claim(any())).thenReturn(invalidEvent);
        Mockito.when(task.assign(any(), any(), any())).thenReturn(invalidEvent);
        return task;
    }

    private gov.uk.ets.registry.api.auditevent.DomainEvent mockEvent(Task task) {
        return gov.uk.ets.registry.api.auditevent.DomainEvent.builder()
            .who("LastName FirstName")
            .when(new Date())
            .what(EventType.TASK_REQUESTED.toString())
            .domainObject(DomainObject.create(Task.class, String.valueOf(task.getRequestId())))
            .description("test comment")
            .build();
    }

    private Task mockTask(Long requestId, RequestType requestType) {
        Task task = mock(Task.class);
        Mockito.when(task.getType()).thenReturn(requestType);
        Mockito.when(task.getRequestId()).thenReturn(requestId);
        return task;
    }


    private TaskActionException createExpectedException(String code, Long... requestId) {
        if (requestId.length == 0) {
            return TaskActionException.create(TaskActionError.builder()
                .code(code)
                .build());
        }
        TaskActionException expectedException = new TaskActionException();
        for (Long id : requestId) {
            expectedException.addError(TaskActionError.builder()
                .code(code)
                .requestId(id)
                .build());
        }

        return expectedException;
    }

    public void mockPermissionsForClaimSuccess() {
        Mockito.when(authService.getScopes())
            .thenReturn(Stream.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()).collect(
                Collectors.toSet()));
    }

    public void mockPermissionsForAssignSuccess() {
        Mockito.when(authService.getScopes())
            .thenReturn(Stream.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName()).collect(
                Collectors.toSet()));
    }

    @Builder
    private static class TestBulkActionCommand {
        private TaskActionException expectedException;
        private String urid;
        private String comment;
        private List<Long> requestIds;
        private List<Task> requestIdsTasksResult;
        private Runnable action;
    }


    @Builder
    private static class TestCommentCommand {

        private boolean claimant;
        private boolean completed;
        private String comment;
        private Long requestId;
        private boolean checkTaskNotFound;
        private Exception expectedException;
    }

    @Builder
    private static class TestCompleteCommand {

        private RequestType requestType;
        private boolean notClaimant;
        private boolean notPermitted;
        private Long requestId;
        private TaskOutcome outcome;
        private String comment;
    }
}
