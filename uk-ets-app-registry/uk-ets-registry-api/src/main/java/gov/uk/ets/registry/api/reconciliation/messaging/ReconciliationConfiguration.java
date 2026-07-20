package gov.uk.ets.registry.api.reconciliation.messaging;

import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

/**
 * Kafka template configuration for reconciliation process.
 */
@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class ReconciliationConfiguration {

    @Value("${kafka.reconciliation-uktl.consumer.group-id:reconciliation-uktl-registry-group}")
    private String reconciliationUktlAnswerConsumerGroup;
    
    @Value("${kafka.reconciliation-uktl.consumer.json.trusted.package:*")
    private String reconciliationAnswerTrustedPackages;

    @Value("${kafka.reconciliation-uktl.question.transactional.id}")
    private String reconciliationUktlQuestionTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;


    @Bean("uktlReconciliationProducerTemplate")
    KafkaTemplate<String, ReconciliationSummary> uktlReconciliationProducerTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(reconciliationUktlQuestionTransactionalId, null);
    }

    @Bean("uktlReconciliationConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> uktlReconciliationConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ReconciliationSummary.class, reconciliationUktlAnswerConsumerGroup));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
