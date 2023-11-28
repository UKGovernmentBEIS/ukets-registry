package gov.uk.ets.publication.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.Serializable;
import java.util.Map;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class PublicationApiMessagingConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.report.consumer.group.id}")
    private String publicationApiConsumerGroupId;

    @Value("${kafka.report.consumer.json.trusted.package")
    private String publicationApiTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaProperties kafkaProperties;

    private final ObjectMapper objectMapper;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return new KafkaConsumerConfig(kafkaProperties);
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig(KafkaProperties kafkaProperties) {
        return new KafkaProducerConfig(kafkaProperties);
    }

    @Bean("publicationApiKafkaTemplate")
    public KafkaTemplate<String, Serializable> kafkaTemplate(KafkaProducerConfig kafkaProducerConfig) {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uk-ets-reports")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(kafkaProducerConfig)
                    .build());
    }

    @Bean("publicationApiListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ReportGenerationEvent> consumerFactory() {
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                stringDeserializer,
                reportGenerationEventDeserializer()
            );
        }
    }

    /**
     * Use spring boot autoconfigured Jackson ObjectMapper to align with deserialization of DTOs.
     */
    private JsonDeserializer<ReportGenerationEvent> reportGenerationEventDeserializer() {
        try (JsonDeserializer<ReportGenerationEvent> deserializer =
                 new JsonDeserializer<>(ReportGenerationEvent.class, objectMapper)
                     .ignoreTypeHeaders()) {
            deserializer.addTrustedPackages(publicationApiTrustedPackages);
            return deserializer;
        }
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = kafkaConsumerConfig(kafkaProperties).getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, publicationApiConsumerGroupId);
        return props;
    }

}
