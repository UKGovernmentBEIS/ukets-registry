package gov.uk.ets.compliance.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.domain.events.base.ComplianceEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

import java.io.Serializable;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class MessagingConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.compliance.consumer.group.id:group.compliance.calculation.service.consumer.group}")
    private String complianceConsumerGroupId;

    @Value("${kafka.compliance.consumer.json.trusted.packages:*")
    private String complianceTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final ObjectMapper objectMapper;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return new KafkaConsumerConfig(kafkaProperties);
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig(KafkaProperties kafkaProperties) {
        return new KafkaProducerConfig(kafkaProperties);
    }


    @Bean("complianceKafkaTemplate")
    public KafkaTemplate<String, Serializable> kafkaTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
                .createTransactionalKafkaTemplate(
                        UkEtsKafkaConfigProperties.builder()
                                .maxAgeInMillis(maxAgeInMillis)
                                .transactionalId("tx-uk-ets-compliance-calculation-service")
                                .kafkaBootstrapAddress(kafkaBootstrapAddress)
                                .kafkaProducerConfig(producerConfig)
                                .build());
    }

    @Bean("complianceListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComplianceEvent> kafkaListenerContainerFactory(
            KafkaConsumerConfig consumerConfig) {
        ConcurrentKafkaListenerContainerFactory<String, ComplianceEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(consumerConfig));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ComplianceEvent> consumerFactory(KafkaConsumerConfig consumerConfig) {
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(
                    consumerConfigs(consumerConfig),
                    stringDeserializer,
                    complianceGenerationEventDeserializer()
            );
        }
    }

    /**
     * Use spring boot autoconfigured Jackson ObjectMapper so as to align with deserialization of DTOs.
     *
     * @return ErrorHandlingDeserializer to be able to handle deserialization exceptions
     */
    private ErrorHandlingDeserializer<ComplianceEvent> complianceGenerationEventDeserializer() {
        try (JsonDeserializer<ComplianceEvent> deserializer =
                     new JsonDeserializer<>(ComplianceEvent.class, objectMapper)
                             .ignoreTypeHeaders()) {
            deserializer.addTrustedPackages(complianceTrustedPackages);
            return new ErrorHandlingDeserializer<>(deserializer);
        }
    }

    private Map<String, Object> consumerConfigs(KafkaConsumerConfig consumerConfig) {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, complianceConsumerGroupId);
        return props;
    }
}
