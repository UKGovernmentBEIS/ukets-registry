package gov.uk.ets.registry.api.user.messaging;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;

@Configuration
@RequiredArgsConstructor
public class KeycloakEventConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.keycloak.event.consumer.group-id:keycloak.event.consumer.group}")
    private String keycloakEventConsumerGroup;

    @Value("${kafka.keycloak.event.consumer.json.trusted.packages:*}")
    private String trustedPackages;

    private final KafkaConsumerConfig consumerConfig;

    @Bean("keycloakEventConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    private ConsumerFactory<String, KeycloakEvent> consumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, keycloakEventConsumerGroup);

        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory(props,
                stringDeserializer,
                getKeycloakEventJsonDeserializer());
        }

    }

    private JsonDeserializer<KeycloakEvent> getKeycloakEventJsonDeserializer() {

        try (JsonDeserializer<KeycloakEvent> jsonDeserializer = new JsonDeserializer<>(KeycloakEvent.class)
            .ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(trustedPackages);
            return jsonDeserializer;
        }
    }
}
