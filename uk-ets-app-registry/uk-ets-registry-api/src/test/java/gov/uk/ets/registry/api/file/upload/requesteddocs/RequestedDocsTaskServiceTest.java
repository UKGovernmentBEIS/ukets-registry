package gov.uk.ets.registry.api.file.upload.requesteddocs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


class RequestedDocsTaskServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;
    
    @Mock
    private TaskRepository taskRepository;
    
    private RequestedDocsTaskService requestedDocsTaskService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        requestedDocsTaskService = new RequestedDocsTaskService(userService,eventService, taskRepository);
    }
    
    @Test
    @DisplayName("Rejecting parent task should also reject child document tasks.")
    void rejectChildRequestedDocumentTasks() {
        Long parentTaskIdentifier = 1L;
        User user = new User();
        user.setUrid("123456");
        Mockito.when(taskRepository.findSubTasksParentRequestId(parentTaskIdentifier))
            .thenReturn(List.of(
                childTask(2L,RequestType.AR_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.SUBMITTED_NOT_YET_APPROVED),
                childTask(3L,RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        requestedDocsTaskService.completeChildRequestedDocumentTasks(parentTaskIdentifier,TaskOutcome.REJECTED);
        
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, Mockito.times(2)).save(argument.capture());
        assertEquals(user, argument.getValue().getCompletedBy());
        assertEquals(RequestStateEnum.REJECTED, argument.getValue().getStatus());
        
        ArgumentCaptor<String> identifierArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uridArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EventType> eventTypeArgument = ArgumentCaptor.forClass(EventType.class);
        ArgumentCaptor<String> actionArgument = ArgumentCaptor.forClass(String.class);
        verify(eventService, Mockito.times(2)).createAndPublishEvent(identifierArgument.capture(),
            uridArgument.capture(),
            descriptionArgument.capture(),
            eventTypeArgument.capture(),
            actionArgument.capture());

        assertEquals(user.getUrid(), uridArgument.getValue());
        assertEquals("Task auto-completed due to parent task rejection.",descriptionArgument.getValue());
        assertEquals(EventType.TASK_REJECTED,eventTypeArgument.getValue());
        assertEquals("Task completed.",actionArgument.getValue());
    }
    
    @Test
    @DisplayName("Approving a parent task should also approve child document tasks.")
    void approveChildRequestedDocumentTasks() {
        Long parentTaskIdentifier = 1L;
        User user = new User();
        user.setUrid("123456");
        Mockito.when(taskRepository.findSubTasksParentRequestId(parentTaskIdentifier))
            .thenReturn(List.of(
                childTask(2L,RequestType.AR_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.SUBMITTED_NOT_YET_APPROVED),
                childTask(3L,RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        requestedDocsTaskService.completeChildRequestedDocumentTasks(parentTaskIdentifier,TaskOutcome.APPROVED);
        
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, Mockito.times(2)).save(argument.capture());
        assertEquals(user, argument.getValue().getCompletedBy());
        assertEquals(RequestStateEnum.APPROVED, argument.getValue().getStatus());
        
        ArgumentCaptor<String> identifierArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uridArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EventType> eventTypeArgument = ArgumentCaptor.forClass(EventType.class);
        ArgumentCaptor<String> actionArgument = ArgumentCaptor.forClass(String.class);
        verify(eventService, Mockito.times(2)).createAndPublishEvent(identifierArgument.capture(),
            uridArgument.capture(),
            descriptionArgument.capture(),
            eventTypeArgument.capture(),
            actionArgument.capture());

        assertEquals(user.getUrid(), uridArgument.getValue());
        assertEquals("Task auto-completed due to parent task approval.",descriptionArgument.getValue());
        assertEquals(EventType.TASK_APPROVED,eventTypeArgument.getValue());
        assertEquals("Task completed.",actionArgument.getValue());
    }
    
    @Test
    @DisplayName("Approving a parent task should not alter completed child document tasks.")
    void approveCompletedChildRequestedDocumentTasks() {
        Long parentTaskIdentifier = 1L;
        User user = new User();
        user.setUrid("123456");
        Mockito.when(taskRepository.findSubTasksParentRequestId(parentTaskIdentifier))
            .thenReturn(List.of(
                childTask(2L,RequestType.AR_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.APPROVED),
                childTask(3L,RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,RequestStateEnum.REJECTED)));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        requestedDocsTaskService.completeChildRequestedDocumentTasks(parentTaskIdentifier,TaskOutcome.APPROVED);
        
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, never()).save(argument.capture());
        
        ArgumentCaptor<String> identifierArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> uridArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EventType> eventTypeArgument = ArgumentCaptor.forClass(EventType.class);
        ArgumentCaptor<String> actionArgument = ArgumentCaptor.forClass(String.class);
        verify(eventService, Mockito.never()).createAndPublishEvent(identifierArgument.capture(),
            uridArgument.capture(),
            descriptionArgument.capture(),
            eventTypeArgument.capture(),
            actionArgument.capture());
    }
    
    private Task childTask(Long requestId, RequestType requestType,RequestStateEnum status) {
        Task task = new Task();
        task.setType(requestType);
        task.setRequestId(requestId);
        task.setStatus(status);
        return task;
    }
}
