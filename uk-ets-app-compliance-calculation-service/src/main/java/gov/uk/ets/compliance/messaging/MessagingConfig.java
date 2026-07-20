package gov.uk.ets.compliance.messaging;

import gov.uk.ets.compliance.domain.events.base.ComplianceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

import java.io.Serializable;

@EnableKafka
@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class MessagingConfig {

    @Value("${kafka.compliance.consumer.group.id:compliance-calculation-service-consumer-registry-group}")
    private String complianceConsumerGroupId;

    @Value("${kafka.compliance.transactional.id}")
    private String complianceTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("complianceKafkaTemplate")
    KafkaTemplate<String, Serializable> complianceKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(complianceTransactionalId, null);
    }

    @Bean("complianceListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ComplianceEvent> complianceListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ComplianceEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ComplianceEvent.class, complianceConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
