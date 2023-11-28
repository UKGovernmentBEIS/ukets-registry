package gov.uk.ets.registry.api.transaction.notification;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.task.domain.TaskCompletionStatus;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: TRANSACTION_PROPOSAL or TRANSACTION_FINALISATION</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class TransactionNotificationAppliance {

    /**
     * Exclude authority users from receiving notifications for these transaction types
     */
    public static final List<TransactionType> TX_TYPES_TO_EXCLUDE_AUTHORITIES = List.of(
        TransactionType.AllocateAllowances,
        TransactionType.ReverseAllocateAllowances,
        TransactionType.ExcessAllocation,
        TransactionType.DeletionOfAllowances,
        TransactionType.SurrenderAllowances,
        TransactionType.ReverseDeletionOfAllowances
    );

    private final GroupNotificationClient groupNotificationClient;
    private final TransactionRepository transactionRepository;
    private final TransactionNotificationBuilder transactionNotificationBuilder;
    private final NotificationService notificationService;
    private final AccountService accountService;

    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used: TRANSACTION_PROPOSAL or TRANSACTION_FINALISATION</li>
     * </ul>
     *
     * @param joinPoint                         the joint point
     * @param emitsGroupNotificationsAnnotation the annotation
     * @param result                            the result of the intercepted method
     */
    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      BusinessCheckResult result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType.equals(GroupNotificationType.TRANSACTION_PROPOSAL)) {
                if (joinPoint.getArgs()[0] instanceof TransactionSummary) {
                    TransactionSummary transactionSummary = (TransactionSummary) joinPoint.getArgs()[0];
                    final TransactionNotificationContext context =
                        getContextOfTransactionProposal(groupNotificationType, transactionSummary, result);
                    if (context != null) {
                        groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(context));
                    }
                } else if (joinPoint.getArgs()[0] instanceof List) {
                    List<TransactionSummary> transactionSummaries = (List<TransactionSummary>) joinPoint.getArgs()[0];
                    for (TransactionSummary summary:transactionSummaries) {
                        final TransactionNotificationContext context =
                            getContextOfTransactionProposal(groupNotificationType, summary, result);
                        if (context != null) {
                            groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(context));
                        }
                    }
                }
            }

            if (groupNotificationType.equals(GroupNotificationType.TRANSACTION_FINALISATION)) {
                final TransactionNotificationContext context =
                    getContextOfTransactionFinalisation(groupNotificationType, joinPoint, result);
                if (context != null) {
                    groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(context));
                }
            }
        }
    }

    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation) {

        Transaction transaction = getTransaction(joinPoint);
        // Completion and reception mail not sent for IssueAllowances
        if (transaction != null && !TransactionType.IssueAllowances.equals(transaction.getType())) {
            for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
                if (isTransactionReceived(groupNotificationType, joinPoint)) {
                    // Notification for acquiring account ARs
                    groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(
                        getContextOfTransactionReception(groupNotificationType, joinPoint)
                    ));
                } else if (groupNotificationType.equals(GroupNotificationType.TRANSACTION_COMPLETION)) {
                    // Notification for transferring account ARs
                    groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(
                        getContextOfTransactionCompletion(groupNotificationType, joinPoint)
                    ));
                }
            }
        }
    }

    @AfterReturning(
        value = "@annotation(emitsGroupNotificationsAnnotation)",
        returning = "result")
    public void applyF(JoinPoint joinPoint,
                       @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                       boolean result) {
        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (groupNotificationType.equals(GroupNotificationType.TRANSACTION_MANUALLY_CANCELLED)) {
                groupNotificationClient.emitGroupNotification(transactionNotificationBuilder.build(
                    getContextOfTransactionCancellation(groupNotificationType, joinPoint, new BusinessCheckResult())
                ));
            }
        }
    }

    private TransactionNotificationContext getContextOfTransactionCancellation(
        GroupNotificationType notificationType, JoinPoint joinPoint, BusinessCheckResult result) {
        String transactionId = (String) joinPoint.getArgs()[0];
        Transaction transaction = transactionRepository.findByIdentifier(transactionId);
        return transactionNotificationContextBuilder(result, transaction, notificationType).build();
    }

    private TransactionNotificationContext.TransactionNotificationContextBuilder transactionNotificationContextBuilder(
        BusinessCheckResult result, Transaction transaction, GroupNotificationType notificationType) {
        Long relatedAccountIdentifier = transaction.getTransferringAccount().getAccountIdentifier();
        Account relatedAccount = accountService.getAccount(relatedAccountIdentifier);

        // request id is not available at the time the notification is sent
        Long requestId = notificationService.findRequestIdForTransaction(transaction.getIdentifier());

        // UKETS-4480: Emails should be sent to all RA when a KP issuance transaction approval
        Set<String> recipients = null;
        if (TransactionType.IssueOfAAUsAndRMUs.equals(transaction.getType())) {
            recipients = notificationService.findEmailsOfAdministrators(false);
        } else {
            AccountType accountTypeEnum = AccountType
                .get(relatedAccount.getRegistryAccountType(), relatedAccount.getKyotoAccountType());
            recipients = notificationService.findEmailsOfArsByAccountIdentifier(relatedAccountIdentifier, false,
                shouldIncludeAuthorityUsers(transaction.getType(), accountTypeEnum));
        }

        return
            TransactionNotificationContext.builder()
                .notificationType(notificationType)
                .emails(recipients)
                .requestId(requestId)
                .transactionSummary(
                    TransactionSummary.transactionSummaryBuilder()
                        .identifier(transaction.getIdentifier())
                        .transferringAccountIdentifier(relatedAccount.getIdentifier())
                        .transferringAccountFullIdentifier(relatedAccount.getFullIdentifier())
                        .quantity(transaction.getQuantity())
                        .type(transaction.getType())
                        .build()
                )
                .businessCheckResult(result);
    }

    private TransactionNotificationContext getContextOfTransactionFinalisation(
        GroupNotificationType notificationType, JoinPoint joinPoint, BusinessCheckResult result) {
        String transactionId = (String) joinPoint.getArgs()[0];
        TaskOutcome taskOutcome = null;
        if (joinPoint.getArgs()[1] instanceof TaskOutcome) {
            taskOutcome = (TaskOutcome) joinPoint.getArgs()[1];
        }
        Transaction transaction = transactionRepository.findByIdentifier(transactionId);

        TransactionNotificationContext.TransactionNotificationContextBuilder transactionNotificationContextBuilder =
            transactionNotificationContextBuilder(result, transaction, notificationType);

        if (taskOutcome != null) {
            transactionNotificationContextBuilder.taskOutcome(TaskCompletionStatus.valueOf(taskOutcome.name()));
            if (TaskCompletionStatus.REJECTED.equals(TaskCompletionStatus.valueOf(taskOutcome.name()))) {
                transactionNotificationContextBuilder.rejectedBy(rejectedBy(transaction));
            }
        }

        return transactionNotificationContextBuilder.build();
    }

    private TransactionNotificationContext getContextOfTransactionProposal(
        GroupNotificationType notificationType, TransactionSummary transactionSummary, BusinessCheckResult result) {
        TransactionNotificationContext context = null;
        Long transferringAccountIdentifier = transactionSummary.getTransferringAccountIdentifier();

        if (transferringAccountIdentifier != null) {
            Account transferringAccount = accountService.getAccount(transferringAccountIdentifier);
            transactionSummary.setTransferringAccountFullIdentifier(transferringAccount.getFullIdentifier());
            AccountType accountTypeEnum = AccountType
                .get(transferringAccount.getRegistryAccountType(), transferringAccount.getKyotoAccountType());
            context = TransactionNotificationContext.builder()
                .emails(notificationService
                    .findEmailsOfArsByAccountIdentifier(transferringAccountIdentifier, false,
                        shouldIncludeAuthorityUsers(transactionSummary.getType(), accountTypeEnum)))
                .notificationType(notificationType)
                .transactionSummary(transactionSummary)
                .businessCheckResult(result)
                .requestId(result.getRequestIdentifier())
                .build();
        }
        return context;
    }


    private TransactionNotificationContext getContextOfTransactionCompletion(
        GroupNotificationType notificationType, JoinPoint joinPoint) {

        Long requestId = null;
        Long transferringAccountIdentifier = null;
        Transaction transaction = getTransaction(joinPoint);
        Account transferringAccount = null;
        if (transaction != null) {
            transferringAccountIdentifier = transaction.getTransferringAccount().getAccountIdentifier();
            transferringAccount = accountService.getAccount(transferringAccountIdentifier);

            if (Boolean.TRUE.equals(transferringAccount.getApprovalOfSecondAuthorisedRepresentativeIsRequired())) {
                requestId = notificationService.findRequestIdForTransaction(transaction.getIdentifier());
            }
        }

        TransactionNotificationContext context = null;
        if (transferringAccountIdentifier != null) {
            AccountType accountTypeEnum = AccountType
                .get(transferringAccount.getRegistryAccountType(), transferringAccount.getKyotoAccountType());
            context = TransactionNotificationContext.builder()
                .emails(notificationService
                    .findEmailsOfArsByAccountIdentifier(transferringAccountIdentifier, true,
                        shouldIncludeAuthorityUsers(transaction.getType(), accountTypeEnum)))
                .notificationType(notificationType)
                .transactionOutcome(transaction.getStatus().name())
                .transactionSummary(
                    TransactionSummary.transactionSummaryBuilder()
                        .identifier(transaction.getIdentifier())
                        .transferringAccountIdentifier(transferringAccount.getIdentifier())
                        .transferringAccountFullIdentifier(transferringAccount.getFullIdentifier())
                        .quantity(transaction.getQuantity())
                        .status(transaction.getStatus())
                        .build()
                )
                .requestId(requestId)
                .transactionId(transaction.getIdentifier())
                .build();
        }
        return context;
    }

    private TransactionNotificationContext getContextOfTransactionReception(
        GroupNotificationType notificationType, JoinPoint joinPoint) {
        UKTLTransactionAnswer answer = (UKTLTransactionAnswer) joinPoint.getArgs()[0];
        Transaction transaction = transactionRepository.findByIdentifier(answer.getTransactionIdentifier());
        Long acquiringAccountIdentifier = transaction.getAcquiringAccount().getAccountIdentifier();

        TransactionNotificationContext context = null;
        if (acquiringAccountIdentifier != null) {
            Account acquiringAccount = accountService.getAccount(acquiringAccountIdentifier);
            AccountType accountTypeEnum = AccountType
                .get(acquiringAccount.getRegistryAccountType(), acquiringAccount.getKyotoAccountType());
            context = TransactionNotificationContext.builder()
                .emails(notificationService
                    .findEmailsOfArsByAccountIdentifier(acquiringAccountIdentifier, true,
                        shouldIncludeAuthorityUsers(transaction.getType(), accountTypeEnum)))
                .notificationType(notificationType)
                .transactionOutcome(transaction.getStatus().name())
                .transactionSummary(
                    TransactionSummary.transactionSummaryBuilder()
                        .identifier(transaction.getIdentifier())
                        .acquiringAccountIdentifier(acquiringAccount.getIdentifier())
                        .acquiringAccountFullIdentifier(acquiringAccount.getFullIdentifier())
                        .quantity(transaction.getQuantity())
                        .status(transaction.getStatus())
                        .build()
                )
                .transactionId(transaction.getIdentifier())
                .build();
        }
        return context;
    }

    private boolean isTransactionReceived(GroupNotificationType groupNotificationType,
                                          JoinPoint joinPoint) {
        if (GroupNotificationType.TRANSACTION_RECEIVED.equals(groupNotificationType)) {
            UKTLTransactionAnswer answer = (UKTLTransactionAnswer) joinPoint.getArgs()[0];
            TransactionStatus status = TransactionStatus.parse(answer.getTransactionStatusCode());
            return TransactionStatus.CHECKED_NO_DISCREPANCY.equals(status) || TransactionStatus.ACCEPTED.equals(status);
        }
        return false;
    }

    private Transaction getTransaction(JoinPoint joinPoint) {
        Transaction transaction = null;
        if (joinPoint.getArgs()[0] instanceof Transaction) {
            transaction = (Transaction) joinPoint.getArgs()[0];
        } else if (joinPoint.getArgs()[0] instanceof UKTLTransactionAnswer) {
            UKTLTransactionAnswer answer = (UKTLTransactionAnswer) joinPoint.getArgs()[0];
            transaction = transactionRepository.findByIdentifier(answer.getTransactionIdentifier());
        }
        return transaction;
    }

    private String rejectedBy(Transaction transaction) {
        Long transferringAccountIdentifier = transaction.getTransferringAccount().getAccountIdentifier();
        Account transferringAccount = accountService.getAccount(transferringAccountIdentifier);
        String rejectedBy;

        if (!transaction.getType().isKyoto()) {
            //is ets
            if (Boolean.TRUE.equals(transferringAccount.getRegistryAccountType().isGovernment())) {
                AccountType accountTypeEnum = AccountType
                    .get(transferringAccount.getRegistryAccountType(), transferringAccount.getKyotoAccountType());
                switch (accountTypeEnum) {
                    case UK_TOTAL_QUANTITY_ACCOUNT:
                    case UK_AUCTION_ACCOUNT:
                    case UK_GENERAL_HOLDING_ACCOUNT:
                    case UK_MARKET_STABILITY_MECHANISM_ACCOUNT:
                    case UK_NEW_ENTRANTS_RESERVE_ACCOUNT:
                        rejectedBy = "an authority user";
                        break;
                    default:
                        rejectedBy = "a registry administrator";
                        break;
                }
            } else {
                rejectedBy = "an authorised representative";
            }
        } else {
            switch (transaction.getType()) {
                case Art37Cancellation:
                case AmbitionIncreaseCancellation:
                case ConversionCP1:
                case ConversionA:
                case ConversionB:
                case TransferToSOPForConversionOfERU:
                case TransferToSOPforFirstExtTransferAAU:
                case Retirement:
                    rejectedBy = "a registry administrator";
                    break;
                default:
                    rejectedBy = "an authorised representative";
                    break;
            }
        }
        return rejectedBy;
    }

    /**
     * Include authority users only if the account  is a non-governmental account
     * and the transaction type is not one of the excluded ones.
     */
    protected boolean shouldIncludeAuthorityUsers(TransactionType txType, AccountType accountType) {
        if (accountType == null) {
            return true;
        }
        return !TX_TYPES_TO_EXCLUDE_AUTHORITIES.contains(txType) && !accountType.isGovernmentAccount() ||
            //Due toUKETS-6783
            TransactionType.CentralTransferAllowances.equals(txType) && accountType.isGovernmentAccount() ||
            //Due to UKETS-6827
            TransactionType.AuctionDeliveryAllowances.equals(txType) && AccountType.UK_AUCTION_ACCOUNT == accountType ||
            TransactionType.ExcessAuction.equals(txType) && AccountType.UK_AUCTION_ACCOUNT == accountType ||
            TransactionType.IssueAllowances.equals(txType) && AccountType.UK_TOTAL_QUANTITY_ACCOUNT == accountType ||
            TransactionType.TransferAllowances.equals(txType) && AccountType.UK_GENERAL_HOLDING_ACCOUNT == accountType;
    }

}
