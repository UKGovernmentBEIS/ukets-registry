package gov.uk.ets.reporting.metrics.messaging.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import gov.uk.ets.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor
public class ReportingMetricsMessagingConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.reporting.metrics.consumer.group.id:group.reporting.metrics.consumer.group}")
    private String reportingMetricsConsumerGroupId;

    @Value("${kafka.reporting.metrics.consumer.json.trusted.packages:*}")
    private String reportingMetricsTrustedPackages;

//    @Value("${kafka.max.age.millis}")
//    private Long maxAgeInMillis;
  
    private final KafkaProperties kafkaProperties;

    private final JsonMapper objectMapper;


    @Bean("reportingMetricsListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, AbstractReportingMetricsEvent> reportingMetricsListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AbstractReportingMetricsEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reportingMetricsConsumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, AbstractReportingMetricsEvent> reportingMetricsConsumerFactory() {
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                stringDeserializer,
                reportingMetricsEventDeserializer()
            );
        }
    }

    /**
     * Use spring boot autoconfigured Jackson ObjectMapper so as to align with deserialization of DTOs.
     *
     * @return ErrorHandlingDeserializer to be able to handle deserialization exceptions
     */
    private ErrorHandlingDeserializer<AbstractReportingMetricsEvent> reportingMetricsEventDeserializer() {
        try (JacksonJsonDeserializer<AbstractReportingMetricsEvent> deserializer =
                 new JacksonJsonDeserializer<>(AbstractReportingMetricsEvent.class, objectMapper)
                     .ignoreTypeHeaders()) {
            deserializer.addTrustedPackages(reportingMetricsTrustedPackages);
            return new ErrorHandlingDeserializer<>(deserializer);
        }
    }
    
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, reportingMetricsConsumerGroupId);
        return props;
    }
    
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
