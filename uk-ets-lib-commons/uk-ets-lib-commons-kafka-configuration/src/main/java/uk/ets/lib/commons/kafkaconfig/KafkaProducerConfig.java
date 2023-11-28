package uk.ets.lib.commons.kafkaconfig;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    // We can use this method to group other common properties across producers.
    public Map<String, Object> getCommonConfigurationProperties() {
        Map<String, Object> securityProperties = kafkaProperties.getSecurity().buildProperties();
        securityProperties.putAll(kafkaProperties.getProperties());
        return securityProperties;

        //TODO maybe this is a drastic change, only use the properties needed not all of the common ones
//        return kafkaProperties.buildProducerProperties();
    }
}
