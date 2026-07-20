package gov.uk.ets.send.email.messaging.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import gov.uk.ets.send.email.messaging.domain.GroupNotification;
import lombok.RequiredArgsConstructor;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class NotificationConfiguration {

    @Value("${kafka.group.notification.consumer.group-id}")
    private String groupNotificationConsumerGroup;
    @Value("${kafka.group.notification.consumer.json.trusted.packages:gov.uk.ets.send.email.messaging.domain.*}")
    private String groupNotificationConsumerJsonTrustedPackages;
    
    private final SharedKafkaConfig sharedKafkaConfig;

    /**
     * Creates a groupNotificationConsumerFactory bean.
     *
     * @return the groupNotificationConsumerFactory bean
     */

    @Bean("kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, GroupNotification> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GroupNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        
        // Get the original consumer factory
        ConsumerFactory<String, GroupNotification> originalFactory =
                sharedKafkaConfig.consumerFactory(GroupNotification.class, groupNotificationConsumerGroup);

        // Copy the unmodifiable map into a mutable one
        Map<String, Object> props = new HashMap<>(originalFactory.getConfigurationProperties());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, GroupNotification.class.getName());
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);   
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, groupNotificationConsumerJsonTrustedPackages);  

        // Create a new consumer factory with the updated properties
        ConsumerFactory<String, GroupNotification> patchedFactory =
                new DefaultKafkaConsumerFactory<>(props);
        
        // Set the patched factory
        factory.setConsumerFactory(patchedFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
