package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;

import java.util.List;

/**
 * Repository for task details.
 */
public interface TaskDetailsRepository {

  /**
   * Gets the details of the task with the given request ID
   *
   * @param requestId
   * @return A task details object
   */
  TaskDetailsDTO getTaskDetails(Long requestId);

  /**
   * Fetch sub tasks of the specific parent task request id
   *
   * @param parentRequestId Parent task request id
   * @return A list of Sub tasks
   */
  List<TaskDetailsDTO> getSubTaskDetails(Long parentRequestId);
}
