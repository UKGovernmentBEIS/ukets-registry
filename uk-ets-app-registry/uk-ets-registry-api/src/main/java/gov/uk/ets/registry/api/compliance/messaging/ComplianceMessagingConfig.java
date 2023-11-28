package gov.uk.ets.registry.api.compliance.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceResponseEvent;
import java.io.Serializable;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@RequiredArgsConstructor
public class ComplianceMessagingConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.compliance.consumer.group.id:group.compliance.consumer.group}")
    private String complianceApiConsumerGroupId;

    @Value("${kafka.compliance.consumer.json.trusted.packages:*")
    private String complianceApiTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaProperties kafkaProperties;

    private final ObjectMapper objectMapper;

    private final KafkaConsumerConfig consumerConfig;

    private final KafkaProducerConfig producerConfig;

    @Bean("complianceApiKafkaTemplate")
    public KafkaTemplate<String, Serializable> complianceApiKafkaTemplate() {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uk-ets-compliance")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    @Bean("complianceApiListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComplianceResponseEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ComplianceResponseEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ComplianceResponseEvent> consumerFactory() {
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
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
    private ErrorHandlingDeserializer<ComplianceResponseEvent> complianceGenerationEventDeserializer() {
        try (JsonDeserializer<ComplianceResponseEvent> deserializer =
                 new JsonDeserializer<>(ComplianceResponseEvent.class, objectMapper)
                     .ignoreTypeHeaders()) {
            deserializer.addTrustedPackages(complianceApiTrustedPackages);
            return new ErrorHandlingDeserializer<>(deserializer);
        }
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, complianceApiConsumerGroupId);
        return props;
    }
}
