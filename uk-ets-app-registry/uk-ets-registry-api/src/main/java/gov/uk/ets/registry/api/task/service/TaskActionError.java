package gov.uk.ets.registry.api.task.service;

import java.io.Serializable;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

/**
 * The Task Bulk action error (claim or assign).
 */
@Getter
@Builder
public class TaskActionError implements Serializable {

    /**
     * Error code for the case that the task is completed.
     */
    public static final String TASK_COMPLETED = "TASK_COMPLETED";
    /**
     * Error code for the case that the task of the request id cannot be found in database.
     */
    public static final String TASK_NOT_FOUND = "TASK_NOT_FOUND";
    /**
     * Error code for the case that the user of the passed urid cannot be found in database.
     */
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    /**
     * Error code for the case that no tasks have been selected for this action.
     */
    public static final String NO_TASKS_SELECTED = "NO_TASKS_SELECTED";
    /**
     * Error code for the case that the Task is not claimed by the assignor.
     */
    public static final String TASK_NOT_CLAIMED_BY_ASSIGNOR = "TASK_NOT_CLAIMED_BY_ASSIGNOR";
    /**
     * Error code for the case that the assignor is not allowed to assign the task to other user.
     */
    public static final String ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TASK = "ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TASK";
    /**
     * Error code for the case that the assignee is not allowed to be assigned with this task.
     */
    public static final String ASSIGNEE_NOT_ALLOWED_TO_BE_ASSIGNED_WITH_TASK =
        "ASSIGNEE_NOT_ALLOWED_TO_BE_ASSIGNED_WITH_TASK";
    /**
     * Error code for the case that the assignor is not allowed to assign this task to this assignee.
     */
    public static final String ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TASK_TO_ASSIGNEE =
        "ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TO_ASSIGNEE";
    /**
     * Error code for the case that the claimant is not allowed to claim the task.
     */
    public static final String INVALID_CLAIMANT = "INVALID_CLAIMANT";
    /**
     * Error code for the case that the task type permission is undefined for the assignor.
     */
    public static final String UNDEFINED_ASSIGNOR_PERMISSION_ON_TASK_TYPE =
        "UNDEFINED_ASSIGNOR_PERMISSION_ON_TASK_TYPE";
    /**
     * Error code for the case that the task type permission is undefined for the assignee.
     */
    public static final String UNDEFINED_ASSIGNEE_PERMISSION_ON_TASK_TYPE =
        "UNDEFINED_ASSIGNEE_PERMISSION_ON_TASK_TYPE";
    /**
     * Error code for the case that the comment is mandatory.
     */
    public static final String COMMENT_REQUIRED = "COMMENT_REQUIRED";
    /**
     * Error code for the case that user who comments on Task is not the claimant of task.
     */
    public static final String USER_SHOULD_BE_THE_CLAIMANT = "USER_SHOULD_BE_THE_CLAIMANT";
    /**
     * Error code for the case that comment is too long, over 1024 chars.
     */
    public static final String COMMENT_TOO_LONG = "COMMENT_TOO_LONG";
    /**
     * Error code for the case that the user does not have permission to complete the given task.
     */
    public static final String NO_PERMISSION_TO_COMPLETE_TASK = "NO_PERMISSION_TO_COMPLETE_TASK";
    /**
     * Error code for the case that user who attempts to complete the is not the claimant of task.
     */
    public static final String USER_SHOULD_BE_THE_CLAIMANT_TO_COMPLETE = "USER_SHOULD_BE_THE_CLAIMANT_TO_COMPLETE";
    /**
     * Error code for the case that user who attempts to complete the task is the initiator of the task.
     */
    public static final String INITIATOR_NOT_ALLOWED_TO_COMPLETE_TASK = "INITIATOR_NOT_ALLOWED_TO_COMPLETE_TASK";
    /**
     * Error code for the case that user who attempts to complete the task is an AR and the task has been initiated
     * by an admin.
     */
    public static final String AR_NOT_ALLOWED_TO_COMPLETE_TASK_INITIATED_BY_ADMIN =
        "AR_NOT_ALLOWED_TO_COMPLETE_TASK_INITIATED_BY_ADMIN";
    /**
     * Error code for the case that the user does not have permission to read the given task.
     */
    public static final String NO_PERMISSION_TO_READ_TASK = "NO_PERMISSION_TO_READ_TASK";
    /**
     * Error code for the exceptional case of which a user exists in registry db but not in keycloak db.
     */
    public static final String USER_NOT_FOUND_IN_KEYCLOAK_DB = "USER_NOT_FOUND_IN_KEYCLOAK_DB";
    /**
     * Error code when attempting to claim already claimed tasks without comment.
     */
    public static final String CLAIM_CLAIMED_WITHOUT_COMMENT = "CLAIM_CLAIMED_WITHOUT_COMMENT";
    /**
     * Error code for the case that the current user changes the account holder id of the open accounting task and it does not exist.
     */
    public static final String ACCOUNT_HOLDER_NON_EXISTENT = "ACCOUNT_HOLDER_NON_EXISTENT";
    /**
     * Error code for the case that the receipt not generated.
     */
    public static final String NOT_PAYMENT_TASK_RECEIPT_GENERATED = "NOT_PAYMENT_TASK_RECEIPT_GENERATED";
    
    /**
     * The error code.
     */
    String code;
    /**
     * The business identifier of task.
     */
    Long requestId;
    /**
     * The business identifier of user.
     */
    String urid;
    /**
     * The error message.
     */
    String message;
    /**
     * The component Identifier.
     */
    String componentId;
    /**
     * The identifier of the error file.
     */
    Long errorFileId;
    /**
     * The name of the error file.
     */
    String errorFilename;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskActionError that = (TaskActionError) o;
        return code.equals(that.code) &&
            Objects.equals(requestId, that.requestId) &&
            Objects.equals(urid, that.urid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, requestId, urid);
    }
}
