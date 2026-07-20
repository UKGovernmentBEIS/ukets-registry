package uk.gov.ets.transaction.log.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.AccountOpeningAnswer;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.messaging.types.TransactionAnswer;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

@EnableKafka
@AutoConfiguration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${kafka.transaction.log.consumer.group.id:transaction-log-messages-registry-group}")
    private String transactionLogConsumerGroupId;

    @Value("${kafka.account-uktl.answer.transactional.id}")
    private String accountOpeningAnswerTransactionalId;

    @Value("${kafka.transaction-uktl.answer.transactional.id}")
    private String transactionAnswerTransactionalId;

    @Value("${kafka.reconciliation-uktl.answer.transactional.id}")
    private String reconciliationAnswerTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("accountOpeningKafkaTemplate")
    KafkaTemplate<String, AccountOpeningAnswer> accountOpeningKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(accountOpeningAnswerTransactionalId, null);
    }

    @Bean("transactionKafkaTemplate")
    KafkaTemplate<String, TransactionAnswer> transactionKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(transactionAnswerTransactionalId, null);
    }

    @Bean("reconciliationKafkaTemplate")
    KafkaTemplate<String, ReconciliationSummary> reconciliationKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(reconciliationAnswerTransactionalId, null);
    }

    @Bean("accountNotificationKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, AccountNotification> accountNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(AccountNotification.class, transactionLogConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("transactionNotificationKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, TransactionNotification> transactionNotificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(TransactionNotification.class, transactionLogConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("reconciliationKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> reconciliationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ReconciliationSummary.class, transactionLogConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
