package gov.uk.ets.registry.api.notification;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

/**
 * Configuration.
 */
@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class NotificationConfiguration {

    private final SharedKafkaConfig sharedKafkaConfig;

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */

    @Bean("groupNotificationProducerTemplate")
    KafkaTemplate<String, Serializable> groupNotificationProducerTemplate() {
        return sharedKafkaConfig.getNonTransactionalKafkaTemplate(null);
    }
}
