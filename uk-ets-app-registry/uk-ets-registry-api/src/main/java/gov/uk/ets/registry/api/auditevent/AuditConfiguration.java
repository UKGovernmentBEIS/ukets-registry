package gov.uk.ets.registry.api.auditevent;

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

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class AuditConfiguration {

    @Value("${kafka.domain.event.consumer.group-id}")
    private String auditCommandConsumerGroup;

    private final SharedKafkaConfig sharedKafkaConfig;

    private final IntegrationKafkaDeadLetterConfiguration integrationKafkaDeadLetterConfiguration;

    @Bean("auditCommandProducerTemplate")
    KafkaTemplate<String, DomainEvent> auditCommandProducerTemplate() {
        return sharedKafkaConfig.getNonTransactionalKafkaTemplate(null);
    }

    @Bean("domainEventConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, DomainEvent> domainEventConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(DomainEvent.class, auditCommandConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
