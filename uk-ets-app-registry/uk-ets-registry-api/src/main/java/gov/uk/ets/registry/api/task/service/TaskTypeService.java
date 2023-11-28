package gov.uk.ets.registry.api.task.service;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.Set;

/**
 * An interface to apply actions related to specific tasks.
 *
 * @param <T> the TaskDetailsDTO for specific {@link RequestType}.
 */
public interface TaskTypeService<T extends TaskDetailsDTO> {

    /**
     * The assigned task types.
     *
     * @return a set of requestTypes that the service handles
     */
    Set<RequestType> appliesFor();

    /**
     * Creates and returns the trusted account task details.
     *
     * @param taskDetailsDTO the base task dto
     * @return a task for trusted accounts dto.
     */
    T getDetails(TaskDetailsDTO taskDetailsDTO);

    /**
     * Perform completion actions for the specific task.
     *
     * @param taskDTO     the input task
     * @param taskOutcome the taskOutcome decision
     * @param comment     the user comment
     */
    TaskCompleteResponse complete(T taskDTO, TaskOutcome taskOutcome, String comment);

    /**
     * Retrieve the specific file for the requested task.
     *
     * @param infoDTO the input task and file info
     * @return the requested file
     */
    default UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return null;
    }

    /**
     * Performs claim actions for the specific task.
     *
     * @param task the input task
     */
    default void claim(Task task) {
    }

    /**
     * Performs assign actions for the specific task.
     *
     * @param task the input task
     */
    default void assign(Task task) {
    }

    /**
     * Implement in the respective service and then annotate them accordingly is special permissions apply.
     */
    default void checkForInvalidAssignPermissions() {
    }

    /**
     * Implement in the respective service and then annotate them accordingly is special permissions apply.
     */
    default void checkForInvalidClaimantPermissions() {
    }


    default TaskCompleteResponse.TaskCompleteResponseBuilder defaultResponseBuilder(T taskDTO) {
        return TaskCompleteResponse.builder().requestIdentifier(taskDTO.getRequestId());
    }

    /**
     * Default method for the task details updates.
     *
     * @param updateInfo the update information.
     * @param taskDetailsDTO the task details DTO.
     * @param taskUpdateAction the task update action.
     */
   default T updateTask(String updateInfo, TaskDetailsDTO taskDetailsDTO, TaskUpdateAction taskUpdateAction) {
       return  null;
   };
}
