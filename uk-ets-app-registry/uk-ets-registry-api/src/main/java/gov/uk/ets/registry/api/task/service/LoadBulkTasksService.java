package gov.uk.ets.registry.api.task.service;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class LoadBulkTasksService {

    private TaskRepository taskRepository;

    public LoadBulkTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Method for loading tasks from request ids.
     *
     * @param requestIds a list of task request identifiers
     * @return a list of tasks
     */
    public List<Task> loadSelectedTasks(List<Long> requestIds) {
        if (CollectionUtils.isEmpty(requestIds)) {
            throw TaskActionException.create(TaskActionError.builder()
                .code(TaskActionError.NO_TASKS_SELECTED)
                .message("No tasks have been selected")
                .build());
        }
        List<Task> tasks = taskRepository.findAllByRequestIdIn(requestIds);
        if (requestIds.size() != tasks.size()) {
            TaskActionException exception = new TaskActionException();
            requestIds.stream().filter(id -> !tasks.stream().map(Task::getRequestId)
                .collect(Collectors.toList()).contains(id)
            ).forEach(id -> exception.addError(TaskActionError.builder()
                .code(TaskActionError.TASK_NOT_FOUND)
                .message("Task with requestId " + id + " does not exist.")
                .requestId(id)
                .build()));
            throw exception;
        }
        return tasks;
    }

    /**
     * Finds the common request type of tasks.
     *
     * @param tasks a list of tasks
     * @return
     */
    public Set<RequestType> getRequestType(List<Task> tasks) {
        return tasks.stream().map(Task::getType).collect(Collectors.toSet());
    }

}
