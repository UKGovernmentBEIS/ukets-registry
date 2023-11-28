package gov.uk.ets.send.email.messaging.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;


@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return new KafkaConsumerConfig(kafkaProperties);
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig(KafkaProperties kafkaProperties) {
        return new KafkaProducerConfig(kafkaProperties);
    }

}
