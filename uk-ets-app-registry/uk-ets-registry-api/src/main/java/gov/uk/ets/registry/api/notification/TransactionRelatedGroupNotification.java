package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.task.domain.TaskCompletionStatus;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * Extends a {@link GroupNotification} with transaction related data.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionRelatedGroupNotification extends EmailNotification {

    private String requestId;
    private String transactionId;
    private Long amount;
    private String accountId;
    private String accountFullId;
    private String estimatedCompletionDate;
    private String estimatedCompletionTime;
    private TaskCompletionStatus taskOutcome;
    private String transactionOutcome;
    private boolean approvalRequired;
    private String rejectedBy;
    private boolean issueAllowances;

    @Builder
    public TransactionRelatedGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId,
                                               String transactionId, Long amount, String accountId, String accountFullId,
                                               String estimatedCompletionDate, String estimatedCompletionTime,
                                               TaskCompletionStatus taskOutcome, String transactionOutcome,
                                               boolean approvalRequired, String rejectedBy, boolean issueAllowances) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.accountId = accountId;
        this.accountFullId = accountFullId;
        this.estimatedCompletionDate = estimatedCompletionDate;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.taskOutcome = taskOutcome;
        this.transactionOutcome = transactionOutcome;
        this.approvalRequired = approvalRequired;
        this.rejectedBy = rejectedBy;
        this.issueAllowances = issueAllowances;
    }
}
