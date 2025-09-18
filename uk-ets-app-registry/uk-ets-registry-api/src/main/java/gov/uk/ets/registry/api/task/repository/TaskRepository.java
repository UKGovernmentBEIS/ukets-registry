package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.file.upload.wrappers.BulkArTaskDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

/**
 * Repository for tasks.
 */
public interface TaskRepository extends JpaRepository<Task, Long>, TaskProjectionRepository, TaskDetailsRepository,
    QuerydslPredicateExecutor<Task> {

    List<Task> findByTypeInAndStatusNotIn(List<RequestType> taskTypes, List<RequestStateEnum> requestStatusList);

    /**
     * Retrieves a task based on its unique business identifier.
     *
     * @param requestId the task business identifier.
     * @return a task.
     */
    Task findByRequestId(Long requestId);

    /**
     * Retrieves the tasks based on their unique business identifiers.
     *
     * @param requestIds a list of request identifiers
     * @return some tasks
     */
    List<Task> findAllByRequestIdIn(List<Long> requestIds);

    /**
     * Retrieves the sub tasks
     *
     * @param parentTaskId The parent task id
     * @return A list of child tasks
     */
    @Query(value = "select task from Task task where task.parentTask.id = ?1")
    List<Task> findSubTasks(Long parentTaskId);

    /**
     * Retrieves the sub tasks
     *
     * @param parentTaskRequestId The parent task request id
     * @return A list of child tasks
     */
    @Query(value = "select task from Task task where task.parentTask.requestId = ?1")
    List<Task> findSubTasksParentRequestId(Long parentTaskRequestId);

    /**
     * Retrieves the latest sub task deadline
     *
     * @param parentTaskId The parent task  id
     * @return The latest deadline
     */
    @Query(value = "select max(task.deadline) from Task task where task.parentTask.id = ?1")
    Optional<Date> findLatestSubTaskDeadline(Long parentTaskId);

    /**
     * Retrieves the task identifier based on the transaction identifier.
     *
     * @param transactionIdentifier the transaction business identifier.
     * @return the task identifier.
     */
    @Query(value = "select task.requestId from Task task inner join task.transactionIdentifiers d where d.transactionIdentifier = :transactionIdentifier")
    Long getTaskIdentifier(@Param("transactionIdentifier") String transactionIdentifier);

    /**
     * /**
     * Retrieves the next business request identifier.
     *
     * @return the next business request identifier.
     */
    @Query(value = "select nextval('request_seq')", nativeQuery = true)
    Long getNextRequestId();

    /**
     * Completes a task.
     *
     * @param requestId     The unique business identifier.
     * @param completedBy   The user who completed the task.
     * @param completedDate The date when the task was completed.
     * @param status        The status.
     * @return the number of updated records
     */
    @Modifying
    @Query("update Task t set t.completedBy = ?2, t.completedDate = ?3, t.status = ?4 where t.requestId = ?1")
    int complete(Long requestId, User completedBy, Date completedDate, RequestStateEnum status);

    /**
     * Retrieves Task for specific Transaction Identifier.
     */
    @Query("select t " +
        "from Task t inner join t.transactionIdentifiers d " +
        "where d.transactionIdentifier = ?1 ")
    Optional<Task> findByTransactionIdentifier(String transactionId);

    /**
     * Retrieve pending tasks for specific Request Type.
     */
    @Query("select t from Task t " +
        "where t.type = ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED")
    List<Task> findPendingTasksByType(RequestType type);

    /**
     * Retrieve pending tasks for specific Request Type and account id.
     *
     * @param type The task type.
     * @param accountId The account ID.
     * @return A list of pending tasks.
     */
    @Query("select t from Task t " +
           "where t.type = ?1 " +
           "and t.account.id = ?2 " +
           "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED")
    List<Task> findPendingTasksByTypeAndAccount(RequestType type, Long accountId);


    /**
     * Retrieve pending tasks for specific Request Type and user.
     */
    @Query("select t from Task t " +
        "where t.type = ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
        "and t.user.urid = ?2")
    List<Task> findPendingTasksByTypeAndUser(RequestType type, String urid);


    /**
     * Retrieve pending tasks for specific list of Request Types and user.
     */
    @Query("select t from Task t " +
        "where t.type in ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
        "and t.user.urid = ?2")
    List<Task> findPendingTasksByTypesAndUser(List<RequestType> type, String urid);

    /**
     * Retrieve pending tasks for specific list of Request Types and users.
     */
    @Query("select t from Task t " +
        "where t.type in ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
        "and t.user.urid in ?2")
    List<Task> findPendingTasksByTypesAndUsers(List<RequestType> type, List<String> urids);

    /**
     * Retrieve pending tasks for specific user.
     */
    @Query("select t from Task t " +
            "where t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
            "and t.user.urid = ?1")
    List<Task> findPendingTasksForUser(String urid);
    
    /**
     * Retrieve pending tasks claimed by specific user.
     */
    @Query("select t from Task t " +
            "where t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
            "and t.claimedBy.urid = ?1")
    List<Task> findPendingTasksClaimedByUser(String urid);

    /**
     * Retrieve pending tasks for specific account excluding ACCOUNT_HOLDER_UPDATE_DETAILS,
     * ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS and AH_REQUESTED_DOCUMENT_UPLOAD.
     */
    @Query("select count(t) from Task t " +
        "where t.account.id = ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
        "and t.type not in (gov.uk.ets.registry.api.task.domain.types.RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS,"
        + "gov.uk.ets.registry.api.task.domain.types.RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,"
        + "gov.uk.ets.registry.api.task.domain.types.RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)")
    Long countPendingTasksByAccountIdExcludingAccountHolderTasks(Long accountId);

    /**
     * Retrieve pending tasks for specific account excluding the Account Holder document tasks.
     */
    @Query("select count(t) from Task t " +
            "where t.account.id = ?1 " +
            "and t.type <> gov.uk.ets.registry.api.task.domain.types.RequestType.AH_REQUESTED_DOCUMENT_UPLOAD " +
            "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED ")
    Long countPendingTasksByAccountIdExcludingAHDocumentTask(Long accountId);

    /**
     * Retrieve pending tasks for specific account excluding a task
     */
    @Query("select count(t) from Task t " +
        "where t.account.id = ?1 " +
        "and t.requestId <> ?2 " +
        "and t.type <> gov.uk.ets.registry.api.task.domain.types.RequestType.AH_REQUESTED_DOCUMENT_UPLOAD " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED ")
    Long countPendingTasksByAccountIdAndRequestIdentifierNotEqual(Long accountId, Long requestIdentifier);

    /**
     * Retrieve pending tasks for specific accounts and type
     */
    @Query("select count(t) from Task t " +
        "where t.account.id in ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED " +
        "and t.type in ?2")
    Long countPendingTasksByAccountIdInAndType(List<Long> accountIdList, List<RequestType> types);

    /**
     * Retrieve pending tasks for specific accounts and type
     */
    @Query("select t from Task t " +
        "inner join gov.uk.ets.registry.api.account.domain.Account a on t.account.id = a.id " +
        "inner join gov.uk.ets.registry.api.account.domain.AccountHolder ah on a.accountHolder.id = ah.id " +
        "where ah.identifier = ?1 " +
        "and t.status = gov.uk.ets.registry.api.task.domain.types.RequestStateEnum.SUBMITTED_NOT_YET_APPROVED")
    List<Task> countPendingTasksByAccountIdentifierInAndType(Long accountHolderId);

    Long countByStatus(RequestStateEnum status);

    /**
     * Retrieves tasks by specific request type.
     *
     * @param type the RequestType.
     * @return a list of BulkArTaskDTO objects.
     */
    @Query(value = "select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArTaskDTO(t.account.fullIdentifier, " +
        "t.user.urid, t.status ) " +
        "from Task t where t.type=?1")
    List<BulkArTaskDTO> retrieveTasksByRequestType(RequestType type);

    /**
     * Retrieves non completed tasks that are mid-way or two days before deadline.
     */
    @Query(value = "select * from task " +
        " where status = 'SUBMITTED_NOT_YET_APPROVED' " +
        " and type in ?2 " +
        " and (cast((claimed_date + (deadline - claimed_date) / 2) as DATE) = ?1 " +
        " or cast(deadline as DATE) - 2 = ?1)", nativeQuery = true)
    List<Task> findMidWayOrTwoDaysBeforeDeadline(LocalDate date, List<String> types);

    /**
     * Retrieves change email tasks by the new email.
     */
    @Query(value = "select * from task " +
        " where status = 'SUBMITTED_NOT_YET_APPROVED' " +
        " and type = 'REQUESTED_EMAIL_CHANGE' " +
        " and cast(difference as json) ->> 'newEmail' = ?1", nativeQuery = true)
    List<Task> findChangeEmailTasksByNewEmail(String newEmail);

    Optional<Task> findByAccount_IdentifierAndTypeAndStatus(Long identifier, RequestType type,
                                                            RequestStateEnum requestStateEnum);
    List<Task> findTasksByType(RequestType type);

    List<Task> findByTypeIn(List<RequestType> types);
}
