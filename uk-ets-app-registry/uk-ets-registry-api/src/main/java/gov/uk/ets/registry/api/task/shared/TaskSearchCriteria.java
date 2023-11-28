package gov.uk.ets.registry.api.task.shared;

import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Search criterias for tasks.
 */
@Getter
@Setter
public class TaskSearchCriteria {

    /**
     * The account number.
     */
    private String accountNumber;

    /**
     * The account holder.
     */
    private String accountHolder;

    /**
     * The task status.
     */
    private TaskStatus taskStatus;

    /**
     * The claimant name.
     */
    private String claimantName;

    /**
     * The task type.
     */
    private RequestType taskType;

    /**
     * The request id.
     */
    private Long requestId;

    /**
     * When the task was claimed (from).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date claimedOnFrom;

    /**
     * When the task was claimed (to).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date claimedOnTo;

    /**
     * When the task was created (from).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOnFrom;

    /**
     * When the task was created (to).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdOnTo;

    /**
     * When the task was completed (from).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date completedOnFrom;

    /**
     * When the task was completed (to).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date completedOnTo;

    /**
     * The transaction identifier.
     */
    private String transactionId;

    /**
     * The task outcome.
     */
    private RequestStateEnum taskOutcome;

    /**
     * The initiator name.
     */
    private String initiatorName;

    /**
     * The account type.
     */
    private String accountType;

    /**
     * The allocation category.
     */
    private String allocationCategory;

    /**
     * The allocation year.
     */
    private String allocationYear;

    /**
     * The user's name or id search parameter
     */
    private String nameOrUserId;

    /**
     * Whether the search should exclude user tasks.
     */
    private Boolean excludeUserTasks;

    /**
     * Which kind of user initiated the request.
     */
    private String initiatedBy;

    /**
     * Which kind of user claimed the request.
     */
    private String claimedBy;

    /**
     * Whether the user performing the search is admin.
     */
    private EndUserSearch endUserSearch;

    /**
     * The user urid eg UK1234
     */
    private String urid;


}
