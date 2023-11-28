package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskActionException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskActionErrorResponse {
    public static TaskActionErrorResponse from(TaskActionException exception) {
        TaskActionErrorResponse response = new TaskActionErrorResponse();
        response.setErrors(exception.getTaskActionErrors());
        return response;
    }

    List<TaskActionError> errors;
}
