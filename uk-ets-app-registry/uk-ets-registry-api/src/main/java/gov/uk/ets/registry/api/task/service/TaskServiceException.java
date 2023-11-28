package gov.uk.ets.registry.api.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The task service-based relative runtime exception.
 */
public class TaskServiceException extends RuntimeException {

    private static final long serialVersionUID = 3450223495927595593L;

    public static TaskServiceException create(TaskActionError error) {
        TaskServiceException exception = new TaskServiceException();
        exception.addError(error);
        return exception;
    }

    private final List<TaskActionError> taskActionErrors = new ArrayList<>();

    /**
     * @return The list of errors
     */
    public List<TaskActionError> getTaskActionErrors() {
        return taskActionErrors;
    }

    /**
     * Adds the error to the error list
     * @param error The {@link TaskActionError} error
     */
    public void addError(TaskActionError error) {
        this.taskActionErrors.add(error);
    }

    /**
     * @return the error messages of its {@link TaskActionError} errors.
     */
    @Override
    public String getMessage() {
        return taskActionErrors.stream()
                               .map(TaskActionError::getMessage)
                               .collect(Collectors.joining("\n"));
    }
}
