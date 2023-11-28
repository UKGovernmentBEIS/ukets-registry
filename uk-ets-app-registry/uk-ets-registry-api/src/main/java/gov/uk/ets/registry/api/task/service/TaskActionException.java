package gov.uk.ets.registry.api.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

/**
 * The Task(s) Bulk Action (claim or assign) relative runtime Exception.
 */
@EqualsAndHashCode(of = "taskBulkActionErrors")
public class TaskActionException extends RuntimeException {

    public static TaskActionException create(TaskActionError error) {
        TaskActionException exception = new TaskActionException();
        exception.addError(error);
        return exception;
    }

    private List<TaskActionError> taskActionErrors = new ArrayList<>();

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
        return String.join("\n", taskActionErrors.stream()
                .map(err -> err.getMessage()).collect(Collectors.toList()));
    }
}
