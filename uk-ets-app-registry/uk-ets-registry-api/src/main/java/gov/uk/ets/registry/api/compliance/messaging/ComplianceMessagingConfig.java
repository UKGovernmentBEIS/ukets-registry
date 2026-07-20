package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;
import uk.ets.lib.kafka.deadletter.IntegrationKafkaDeadLetterConfiguration;

import java.io.Serializable;

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class ComplianceMessagingConfig {

    @Value("${kafka.compliance.consumer.group.id:compliance-consumer-registry-group}")
    private String complianceApiConsumerGroupId;

    @Value("${kafka.compliance.events.in.transactional.id}")
    private String complianceTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    private final IntegrationKafkaDeadLetterConfiguration integrationKafkaDeadLetterConfiguration;

    @Bean("complianceApiKafkaTemplate")
    public KafkaTemplate<String, Serializable> complianceApiKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(complianceTransactionalId, null);
    }

    @Bean("complianceApiListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComplianceResponseEvent> complianceApiListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ComplianceResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ComplianceResponseEvent.class, complianceApiConsumerGroupId));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
