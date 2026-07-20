package gov.uk.ets.registry.api.transaction.config;

import gov.uk.ets.registry.api.transaction.messaging.TransactionNotification;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

@Configuration
@RequiredArgsConstructor
public class TransactionConfiguration {

    @Value("${kafka.integration.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.transaction-uktl.consumer.group-id}")
    private String transactionUktlAnswerConsumerGroup;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    @Value("${kafka.transaction-uktl.question.transactional.id}")
    private String transactionUktlQuestionTransactionalId;

    private final SharedKafkaConfig integrationKafkaAuthenticationConfig;

    @Bean("uktlTransactionProducerTemplate")
    KafkaTemplate<String, TransactionNotification> uktlTransactionProducerTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(transactionUktlQuestionTransactionalId, null);
    }

    @Bean("uktlTransactionConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, UKTLTransactionAnswer> uktlTransactionConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UKTLTransactionAnswer> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(UKTLTransactionAnswer.class, transactionUktlAnswerConsumerGroup));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
