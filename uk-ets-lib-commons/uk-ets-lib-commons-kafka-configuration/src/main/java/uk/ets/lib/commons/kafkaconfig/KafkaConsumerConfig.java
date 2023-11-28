package uk.ets.lib.commons.kafkaconfig;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Retrieves the security related properties and also other common consumer properties (if applicable).
     */
    public Map<String, Object> getCommonConfigurationProperties() {
        Map<String, Object> props = kafkaProperties.getSecurity().buildProperties();
        // this is needed only if some properties are defined in property file (with prefix 'spring.kafka.properties.')
        props.putAll(kafkaProperties.getProperties());
        // read_committed is required when using transactions in kafka. if the transactions are not enabled this property has no effect.
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return props;
    }
}
