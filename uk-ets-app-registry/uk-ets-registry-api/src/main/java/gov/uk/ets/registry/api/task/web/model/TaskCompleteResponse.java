package gov.uk.ets.registry.api.task.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * The response object to a task completion action.
 * TODO:  This shall be replaced with the inner TaskDetailsDTO object, which has been temporarily added
 * as a nested property in order to minimize the required changes.
 * Since the user always need to have an updated version of the task after a complete request there is no
 * point to return minimized responses and then forcing them to fetch the task details once more.
 * It is more straight forward to immediately return the updated task after the server side actions.
 */
@Data
@Builder
@AllArgsConstructor
public class TaskCompleteResponse {
    /**
     * The business identifier of the task.
     */
    private Long requestIdentifier;

    // the updatedTask after completion
    private TaskDetailsDTO taskDetailsDTO;
}
