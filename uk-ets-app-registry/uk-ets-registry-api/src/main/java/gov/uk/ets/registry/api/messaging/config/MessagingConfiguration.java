package gov.uk.ets.registry.api.messaging.config;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;


@Configuration
public class MessagingConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    /**
     * Generates a transactional ProducerFactory.
     *
     * @return the ProducerFactory.
     */
    @Bean
    @Primary
    public ProducerFactory<String, Serializable> producerFactory(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createTransactionalKafkaProducerFactory(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uktl-account-notify")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    /**
     * Generates a KafkaTemplate.
     *
     * @return the KafkaTemplate.
     */
    @Bean("producerTemplate")
    @Primary
    public KafkaTemplate<String, Serializable> transactionProducerTemplate(KafkaProducerConfig producerConfig) {
        return new KafkaTemplate<>(producerFactory(producerConfig));
    }
}
