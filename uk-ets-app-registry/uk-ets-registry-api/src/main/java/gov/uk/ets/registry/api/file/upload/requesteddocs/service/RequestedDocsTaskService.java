package gov.uk.ets.registry.api.file.upload.requesteddocs.service;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestedDocsTaskService {

    private final UserService userService;

    private final EventService eventService;
    
    private final TaskRepository taskRepository;
    
    /**
     * Completes the SUBMITTED_NOT_YET_APPROVED child AH_REQUESTED_DOCUMENT_UPLOAD,AR_REQUESTED_DOCUMENT_UPLOAD tasks 
     * when the parent is also approved or rejected.
     * @param parentTaskId the identifier of the parent task
     */
    public void completeChildRequestedDocumentTasks(Long parentTaskId,TaskOutcome outcome) {
        String description = TaskOutcome.APPROVED.equals(outcome) ? "Task auto-completed due to parent task approval." : "Task auto-completed due to parent task rejection.";
        User currentUser = userService.getCurrentUser();
        List<Task> subTasks = taskRepository.findSubTasksParentRequestId(parentTaskId);
        subTasks.stream()
        .filter(t-> RequestStateEnum.SUBMITTED_NOT_YET_APPROVED.equals(t.getStatus()))
        .filter(childTask -> EnumSet.of(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
            RequestType.AR_REQUESTED_DOCUMENT_UPLOAD)
            .contains(childTask.getType()))           
            .forEach(childTask -> {
                childTask.setCompletedDate(new Date());
                childTask.setStatus(TaskOutcome.APPROVED.equals(outcome) ? RequestStateEnum.APPROVED : RequestStateEnum.REJECTED);
                childTask.setCompletedBy(currentUser);
                taskRepository.save(childTask);                
                eventService.createAndPublishEvent(String.valueOf(childTask.getRequestId()), currentUser.getUrid(),
                    description,
                    TaskOutcome.APPROVED.equals(outcome) ? EventType.TASK_APPROVED : EventType.TASK_REJECTED,
                    "Task completed.");
            });        
    }
}
