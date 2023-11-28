package gov.uk.ets.registry.api.transaction.notification;

import gov.uk.ets.registry.api.task.domain.TaskCompletionStatus;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.Builder;

/**
 * This is a data storage class that keeps all the information needed to create and sent
 * notifications to a set of recipients. It is not a dumb storage, it also performs some logic.
 * It is used by all the Appliance (aspect) classes that process the
 * {@link gov.uk.ets.registry.usernotifications.EmitsGroupNotifications} annotation.
 */
@Builder
public class TransactionNotificationContext {
    private final GroupNotificationType notificationType;
    private final String transactionId;
    private final TransactionSummary transactionSummary;
    private final TaskCompletionStatus taskOutcome;
    private final BusinessCheckResult businessCheckResult;
    private final String transactionOutcome;
    private final Set<String> emails;
    private final Long requestId;
    private final String rejectedBy;

    /**
     * An empty context.
     *
     * @return empty context.
     */
    public static TransactionNotificationContext empty() {
        return TransactionNotificationContext.builder().build();
    }

    /**
     * The notification type.
     *
     * @return the notification type for which this context was built.
     */
    public GroupNotificationType notificationType() {
        return notificationType;
    }

    /**
     * The task outcome.
     *
     * @return the task outcome as it came from the user
     */
    public TaskCompletionStatus taskOutcome() {
        return taskOutcome;
    }

    /**
     * The transaction id in the context.
     *
     * @return the transaction id
     */
    public String transactionId() {
        return transactionId == null && transactionSummary != null
            ? transactionSummary.getIdentifier()
            : transactionId;
    }

    /**
     * The quantity that is transferred in this transaction.
     *
     * @return the quantity transferred in this transaction
     */
    public Long quantity() {
        if (transactionSummary != null && transactionSummary.getQuantity() != null) {
            return transactionSummary.getQuantity();
        } else {
            return transactionSummary == null ? null : transactionSummary.calculateQuantity();
        }

    }

    /**
     * The account id of this context.
     *
     * @return the account id
     */
    public String accountId() {
        return transactionSummary == null ? null : String.valueOf(
            theRelatedAccountIdentifier()
        );
    }

    /**
     * Depending on the notification type it can either the acquiring or transferring account.
     *
     * @return the related account identifier
     */
    public Long theRelatedAccountIdentifier() {
        return GroupNotificationType.TRANSACTION_INBOUND.equals(notificationType) ||
            GroupNotificationType.TRANSACTION_RECEIVED.equals(notificationType)
            ? transactionSummary.getAcquiringAccountIdentifier()
            : transactionSummary.getTransferringAccountIdentifier();
    }

    /**
     * The account id of this context.
     *
     * @return the account id
     */
    public String accountFullId() {
        return transactionSummary == null ? null : String.valueOf(
            theRelatedAccountFullIdentifier()
        );
    }

    /**
     * Depending on the notification type it can either the acquiring or transferring account.
     *
     * @return the related account full identifier
     */
    public String theRelatedAccountFullIdentifier() {
        return GroupNotificationType.TRANSACTION_INBOUND.equals(notificationType) ||
            GroupNotificationType.TRANSACTION_RECEIVED.equals(notificationType)
            ? transactionSummary.getAcquiringAccountFullIdentifier()
            : transactionSummary.getTransferringAccountFullIdentifier();
    }

    /**
     * The estimated completion date.
     *
     * @return the estimated completion date
     */
    public String estimatedCompletionDate() {
        return businessCheckResult == null ? null : businessCheckResult.getExecutionDate();
    }

    /**
     * The estimated completion time.
     *
     * @return the estimated completion time
     */
    public String estimatedCompletionTime() {
        return businessCheckResult == null ? null : businessCheckResult.getExecutionTime();
    }

    /**
     * The request id.
     *
     * @return the request id
     */
    public String requestId() {
        return requestId == null ? null : String.valueOf(requestId);
    }

    /**
     * Who rejected the request.
     *
     * @return rejected by
     */
    public String rejectedBy() {
        return rejectedBy == null ? null : String.valueOf(rejectedBy);
    }

    /**
     * The transaction outcome.
     *
     * @return the transaction outcome
     */
    public String transactionOutcome() {
        return transactionOutcome;
    }

    /**
     * Is approval required?
     *
     * @return true if approval is required
     */
    public boolean approvalRequired() {
        Boolean approval = businessCheckResult == null ? Boolean.FALSE : businessCheckResult.getApprovalRequired();
        return approval == null ? Boolean.FALSE : approval;
    }

    /**
     * Is issue allowances request?
     *
     * @return true if approval is required
     */
    public boolean issueAllowances() {
        return TransactionType.IssueAllowances.equals(transactionSummary.getType());
    }

    /**
     * Returns the emails that will be used when sending out the notification.
     *
     * @return the emails of the recipients
     */
    public Set<String> emails() {
        return emails;
    }


}
