package gov.uk.ets.registry.api.transaction.notification;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryCode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Optional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>The following {@link GroupNotificationType} was used: TRANSACTION_INBOUND</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class InboundTransactionNotificationAppliance {

    private static final String COMPLETED_OUTCOME_TEXT = "completed";
    private static final String FAILED_OUTCOME_TEXT = "failed";

    private final GroupNotificationClient groupNotificationClient;
    private final TransactionRepository transactionRepository;
    private final TaskRepository taskRepository;
    private final TransactionNotificationBuilder notificationPreparation;

    private final NotificationService notificationService;
    private final AccountService accountService;

    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>The following {@link GroupNotificationType} was used: TRANSACTION_INBOUND</li>
     * </ul>
     *
     * @param joinPoint                         the joint point
     * @param emitsGroupNotificationsAnnotation the annotation
     */
    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {

            if (isCompletedInboundTransaction(groupNotificationType, joinPoint)) {
                final TransactionNotificationContext context =
                    getContextOfInboundTransaction(groupNotificationType, joinPoint);
                if (context != null) {
                    groupNotificationClient.emitGroupNotification(notificationPreparation.build(context));
                }
            } else if (GroupNotificationType.TRANSACTION_INBOUND_COMPLETION.equals(groupNotificationType)) {
                final TransactionNotificationContext context =
                    getContextOfInboundTransactionCompletion(groupNotificationType, joinPoint);
                if (context != null) {
                    groupNotificationClient.emitGroupNotification(notificationPreparation.build(context));
                }
            }
        }
    }

    private boolean isCompletedInboundTransaction(GroupNotificationType groupNotificationType,
                                                  JoinPoint joinPoint) {
        if (GroupNotificationType.TRANSACTION_INBOUND.equals(groupNotificationType)) {
            NotificationRequest request = (NotificationRequest) joinPoint.getArgs()[0];
            TransactionStatus status = TransactionStatus.parse(request.getTransactionStatus());
            return TransactionStatus.CHECKED_NO_DISCREPANCY.equals(status) || TransactionStatus.ACCEPTED.equals(status);

        }
        return false;
    }

    private TransactionNotificationContext getContextOfInboundTransaction(GroupNotificationType notificationType,
                                                                          JoinPoint joinPoint) {
        NotificationRequest request = (NotificationRequest) joinPoint.getArgs()[0];
        String transactionIdentifier = request.getTransactionIdentifier();
        Transaction transaction = transactionRepository.findByIdentifier(transactionIdentifier);
        // For external transfers, from UKETS to an external registry, do not send reception mail to acquiring account
        if (!isUketsAccount(transaction.getAcquiringAccount().getAccountRegistryCode())) {
            return null;
        }

        TransactionStatus status = TransactionStatus.parse(request.getTransactionStatus());
        Optional<Task> task = taskRepository
            .findByTransactionIdentifier(transactionIdentifier);

        Long relatedAccountIdentifier = transaction.getAcquiringAccount().getAccountIdentifier();

        Account relatedAccount = accountService.getAccount(relatedAccountIdentifier);

        return TransactionNotificationContext.builder()
            .notificationType(notificationType)
            .transactionOutcome(transactionOutcome(status))
            .emails(notificationService.findEmailsOfArsByAccountIdentifier(relatedAccountIdentifier, true))
            .transactionSummary(
                TransactionSummary.transactionSummaryBuilder()
                    .identifier(transaction.getIdentifier())
                    .acquiringAccountIdentifier(relatedAccount.getIdentifier())
                    .acquiringAccountFullIdentifier(relatedAccount.getFullIdentifier())
                    .quantity(transaction.getQuantity())
                    .taskIdentifier(task.map(Task::getRequestId).orElse(null))
                    .build()
            )
            .build();
    }


    private TransactionNotificationContext getContextOfInboundTransactionCompletion(
        GroupNotificationType notificationType, JoinPoint joinPoint) {

        NotificationRequest request = (NotificationRequest) joinPoint.getArgs()[0];
        String transactionIdentifier = request.getTransactionIdentifier();
        Transaction transaction = transactionRepository.findByIdentifier(transactionIdentifier);
        // For external transfers, from another registry to UKETS, do not send completion mail to transferring account
        if (!isUketsAccount(transaction.getTransferringAccount().getAccountRegistryCode())) {
            return null;
        }
        TransactionStatus status = TransactionStatus.parse(request.getTransactionStatus());
        Optional<Task> task = taskRepository
            .findByTransactionIdentifier(transactionIdentifier);
        Long transferringAccountIdentifier = transaction.getTransferringAccount().getAccountIdentifier();

        Account transferringAccount = accountService.getAccount(transferringAccountIdentifier);
        return TransactionNotificationContext.builder()
            .notificationType(notificationType)
            .transactionOutcome(transactionCompletionOutcome(status))
            .emails(notificationService.findEmailsOfArsByAccountIdentifier(transferringAccountIdentifier, true))
            .requestId(task.map(Task::getRequestId).orElse(null))
            .transactionSummary(
                TransactionSummary.transactionSummaryBuilder()
                    .identifier(transaction.getIdentifier())
                    .transferringAccountIdentifier(transferringAccount.getIdentifier())
                    .transferringAccountFullIdentifier(transferringAccount.getFullIdentifier())
                    .quantity(transaction.getQuantity())
                    .taskIdentifier(task.map(Task::getRequestId).orElse(null))
                    .build()
            )
            .build();
    }

    private String transactionOutcome(TransactionStatus status) {
        String transactionOutcome = "";
        if (TransactionStatus.CHECKED_NO_DISCREPANCY.equals(status) || TransactionStatus.ACCEPTED.equals(status)) {
            transactionOutcome = COMPLETED_OUTCOME_TEXT;
        } else if (TransactionStatus.STL_CHECKED_DISCREPANCY.equals(status) ||
            TransactionStatus.CHECKED_DISCREPANCY.equals(status) || TransactionStatus.REJECTED.equals(status)) {
            transactionOutcome = FAILED_OUTCOME_TEXT;
        } else if (TransactionStatus.CANCELLED.equals(status)) {
            transactionOutcome = FAILED_OUTCOME_TEXT;
        }
        return transactionOutcome;
    }

    // TODO: might be the same with transactionOutcome method above, in that case remove one of the two.
    private String transactionCompletionOutcome(TransactionStatus status) {
        String transactionOutcome;
        switch (status) {
            case COMPLETED:
            case CHECKED_NO_DISCREPANCY:
            case ACCEPTED:
                transactionOutcome = COMPLETED_OUTCOME_TEXT;
                break;
            case REJECTED:
            case STL_CHECKED_DISCREPANCY:
            case CHECKED_DISCREPANCY:
            case CANCELLED:
            case TERMINATED:
                transactionOutcome = FAILED_OUTCOME_TEXT;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
        return transactionOutcome;
    }

    private boolean isUketsAccount(String accountRegistryCode) {
        // If transaction registry code is not UK or GB, then it is not a UKETS account
        return RegistryCode.GB.name().equals(accountRegistryCode) || RegistryCode.UK.name().equals(accountRegistryCode);
    }
}
