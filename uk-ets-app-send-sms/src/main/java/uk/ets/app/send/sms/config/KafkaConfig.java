package uk.ets.app.send.sms.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lombok.RequiredArgsConstructor;
import uk.ets.app.send.sms.domain.SmsNotification;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;
import uk.gov.service.notify.NotificationClient;

@EnableKafka
@Import(SharedKafkaConfig.class)
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${kafka.sms.notification.consumer.group-id}")
    private String smsNotificationConsumerGroup;
    @Value("${kafka.sms.notification.consumer.json.trusted.packages:uk.ets.app.send.sms.domain.*}")
    private String smsNotificationConsumerJsonTrustedPackages;
    
    private final SharedKafkaConfig sharedKafkaConfig;

    /**
     * Creates a groupNotificationConsumerFactory bean.
     *
     * @return the groupNotificationConsumerFactory bean
     */

    @Bean("kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, SmsNotification> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SmsNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        
        // Get the original consumer factory
        ConsumerFactory<String, SmsNotification> originalFactory =
                sharedKafkaConfig.consumerFactory(SmsNotification.class, smsNotificationConsumerGroup);

        // Copy the unmodifiable map into a mutable one
        Map<String, Object> props = new HashMap<>(originalFactory.getConfigurationProperties());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SmsNotification.class.getName());
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);   
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, smsNotificationConsumerJsonTrustedPackages);  

        // Create a new consumer factory with the updated properties
        ConsumerFactory<String, SmsNotification> patchedFactory =
                new DefaultKafkaConsumerFactory<>(props);
        
        // Set the patched factory
        factory.setConsumerFactory(patchedFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
    
    @Bean
    NotificationClient notificationClient(
            @Value("${notification.service.api.key}") String apiKey) {
        return new NotificationClient(apiKey);
    }

}
