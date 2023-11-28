package uk.gov.ets.transaction.log.messaging.kafka.listener;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.service.AccountService;
import uk.gov.ets.transaction.log.service.TransactionService;
import uk.gov.ets.transaction.log.service.reconciliation.ReconciliationService;

@Service
@AllArgsConstructor
public class RegistryIncomingMessageListener {

    /**
     * The service for account.
     */
    private final AccountService accountService;
    /**
     * The service for transactions.
     */
    private final TransactionService transactionService;

    private final ReconciliationService reconciliationService;

    @KafkaListener(
        topics = "registry.originating.notification.topic",
        containerFactory = "accountNotificationKafkaListenerContainerFactory")
    public void handle(AccountNotification accountNotification, Message<AccountNotification> message) {
        accountService.acceptAccountOpeningRequest(accountNotification);
    }


    @KafkaListener(
        topics = "registry.originating.transaction.question.topic",
        containerFactory = "transactionNotificationKafkaListenerContainerFactory")
    public void handle(TransactionNotification transactionNotification, Message<TransactionNotification> message) {
        transactionService.acceptTransactionProposal(transactionNotification);
    }

    @KafkaListener(
        topics = "registry.originating.reconciliation.question.topic",
        containerFactory = "reconciliationKafkaListenerContainerFactory")
    public void handle(ReconciliationSummary reconciliationSummary) {
        reconciliationService.performReconciliation(reconciliationSummary);
    }
}

