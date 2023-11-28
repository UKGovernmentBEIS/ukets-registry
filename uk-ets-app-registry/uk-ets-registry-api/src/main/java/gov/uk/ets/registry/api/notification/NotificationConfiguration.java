package gov.uk.ets.registry.api.notification;

import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

/**
 * Configuration.
 */
@Configuration
@RequiredArgsConstructor
public class NotificationConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */
    @Bean("groupNotificationProducerTemplate")
    public KafkaTemplate<String, Serializable> groupNotificationProducerTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createNonTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }
}
