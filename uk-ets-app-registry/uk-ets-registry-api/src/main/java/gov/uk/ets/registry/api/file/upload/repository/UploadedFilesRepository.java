package gov.uk.ets.registry.api.file.upload.repository;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for the uploaded files.
 */
public interface UploadedFilesRepository extends JpaRepository<UploadedFile, Long> {

    /**
     * Retrieve the uploaded file entities based on their file status and creation date.
     *
     * @param status   the file status
     * @param dateTime the creation date time
     * @return a list of uploaded file entities.
     */
    List<UploadedFile> findByFileStatusEqualsAndCreationDateIsBefore(FileStatus status, LocalDateTime dateTime);

    /**
     * Retrieve a single uploaded file entity based on its file name.
     *
     * @param filename the file name
     * @return an uploaded file entity.
     */
    UploadedFile findFirstByFileNameEquals(String filename);


    /**
     * Retrieve a single allocation table entity based on its file name and its task status if exists.
     *
     * @param fileNameChunk the part of the file name
     * @param status        the task status
     * @return an allocation table entity
     */
    UploadedFile findFirstByFileNameContainsIgnoreCaseAndTaskStatus(String fileNameChunk, RequestStateEnum status);


    /**
     * Retrieve all uploaded file entities based on the task status.
     *
     * @param status        the task status
     * @return uploaded file entities
     */
    List<UploadedFile> findByTaskStatus(RequestStateEnum status);

    /**
     * Retrieve a single uploaded file entity based on the task it is associated with.
     *
     * @param requestId the request ID of the task.
     * @return an uploaded file entity.
     */
    UploadedFile findFirstByTaskRequestId(Long requestId);

    /**
     * retrieves all files related to a speficic task.
     *
     * @param requestId the task request identifier.
     * @return a list of uploaded files.
     */
    List<UploadedFile> findByTaskRequestId(Long requestId);


    /**
     * Retrieve all file names based on the task request identifier.
     *
     * @param requestId  request identifier
     * @return a list of uploaded file names.
     */
    @Query("select f.fileName from UploadedFile f " +
        "where f.task.requestId = ?1")
    List<String> findFileNameByTaskRequestId(Long requestId);

    /**
     * Retrieves all the files related to specific task type for specific time period with parent task
     *
     * @param type            A list of task types
     * @param completedBefore A specific time period
     * @return A list of files
     */
    @Query("select f from UploadedFile f " +
        "where f.task.type in ?1 " +
        "and f.task.status not in (gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED) " +
        "and f.task.parentTask is not null " +
        "and f.task.parentTask.completedDate <= ?2 ")
    List<UploadedFile> findFilesForCompletedTasksWithParent(List<RequestType> type, Date completedBefore);

    /**
     * Retrieves all the files related to specific task type for specific time period with no parent task
     *
     * @param type            A list of task types
     * @param completedBefore A specific time period
     * @return A list of files
     */
    @Query("select f from UploadedFile f " +
        "where f.task.type in ?1 " +
        "and f.task.status not in (gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED) " +
        "and f.task.completedDate <= ?2 " +
        "and f.task.parentTask is null")
    List<UploadedFile> findFilesForCompletedTasksWithNoParent(List<RequestType> type, Date completedBefore);

    /**
     * retrieves all files related to a speficic task request id and file id.
     *
     * @param requestId the task request identifier.
     * @param fileId    the file id.
     * @return an uploaded file entity.
     */
    Optional<UploadedFile> findByTaskRequestIdAndId(Long requestId, Long fileId);
}
