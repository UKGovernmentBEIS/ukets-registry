package gov.uk.ets.registry.api.transaction.notification;

import gov.uk.ets.registry.api.notification.TransactionRelatedGroupNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds transaction specific notifications taking into account a specific context.
 */
@Component
@RequiredArgsConstructor
public class TransactionNotificationBuilder {

    /**
     * Builds the transaction related notification.
     *
     * @param context the context containing all the data that will be used during building
     * @return the notification
     */
    public TransactionRelatedGroupNotification build(TransactionNotificationContext context) {
        return TransactionRelatedGroupNotification
            .builder()
            .requestId(context.requestId())
            .accountId(context.accountId())
            .accountFullId(context.accountFullId())
            .transactionId(context.transactionId())
            .estimatedCompletionDate(context.estimatedCompletionDate())
            .estimatedCompletionTime(context.estimatedCompletionTime())
            .amount(context.quantity())
            .taskOutcome(context.taskOutcome())
            .type(context.notificationType())
            .transactionOutcome(context.transactionOutcome())
            .approvalRequired(context.approvalRequired())
            .recipients(context.emails())
            .rejectedBy(context.rejectedBy())
            .issueAllowances(context.issueAllowances())
            .build();
    }
}
