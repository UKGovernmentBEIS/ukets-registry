package gov.uk.ets.registry.api.user.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import lombok.RequiredArgsConstructor;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class KeycloakEventConfiguration {

    @Value("${kafka.keycloak.event.consumer.group-id:keycloak-event-consumer-registry-group}")
    private String keycloakEventConsumerGroup;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("keycloakEventConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent> keycloakEventConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(KeycloakEvent.class, keycloakEventConsumerGroup));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
