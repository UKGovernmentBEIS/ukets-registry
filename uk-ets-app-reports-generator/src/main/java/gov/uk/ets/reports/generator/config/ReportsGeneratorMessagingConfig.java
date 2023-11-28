package gov.uk.ets.reports.generator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.reports.generator.export.ReportGeneratorService;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolReportGeneratorService;
import gov.uk.ets.reports.generator.messaging.ReportGenerationCommandMessageListener;
import gov.uk.ets.reports.generator.messaging.ReportGenerationCommandMessageListenerErrorHandler;
import gov.uk.ets.reports.generator.messaging.ReportOutcomeMessageService;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
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
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;


/**
 * Configuration.
 */
@Configuration
@RequiredArgsConstructor
public class ReportsGeneratorMessagingConfig {

    private static final String TRANSACTION_ID = "tx-report-generator";

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${spring.kafka.consumer.group-id}")
    private String reportGenerationCommandConsumerGroup;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private String reportGenerationMaxPollRecords;

    @Value("${spring.kafka.consumer.max-poll-interval}")
    private String reportGenerationMaxPollInterval;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    private String trustedPackages;

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

    @Bean
    public KafkaListenerErrorHandler reportGenerationCommandMessageListenerErrorHandler(
        ReportOutcomeMessageService reportOutcomeMessageService) {
        return new ReportGenerationCommandMessageListenerErrorHandler(reportOutcomeMessageService);
    }

    @Bean
    public ReportGenerationCommandMessageListener reportGenerationCommandMessageListener(
            ReportGeneratorService reportGeneratorService, KyotoProtocolReportGeneratorService kyotoProtocolReportGeneratorService) {
        return new ReportGenerationCommandMessageListener(reportGeneratorService, kyotoProtocolReportGeneratorService);
    }

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */
    @Bean("reportProducerTemplate")
    public KafkaTemplate<String, Serializable> reportProducerTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils.createNonTransactionalKafkaTemplate(
            UkEtsKafkaConfigProperties.builder()
                .kafkaBootstrapAddress(kafkaBootstrapAddress)
                .kafkaProducerConfig(producerConfig)
                .build());
    }

    @Bean("reportGenerationCommandConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReportGenerationCommand> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReportGenerationCommand> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    private ConsumerFactory<String, ReportGenerationCommand> consumerFactory() {

        try (StringDeserializer deserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                deserializer,
                getReportGenerationCommandJsonDeserializer());
        }

    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = kafkaConsumerConfig(kafkaProperties).getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, reportGenerationCommandConsumerGroup);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, reportGenerationMaxPollInterval);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, reportGenerationMaxPollRecords);
        return props;
    }

    private JsonDeserializer<ReportGenerationCommand> getReportGenerationCommandJsonDeserializer() {
        try (JsonDeserializer<ReportGenerationCommand> jsonDeserializer = new JsonDeserializer<>(
            ReportGenerationCommand.class, objectMapper).ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(trustedPackages);
            return jsonDeserializer;
        }
    }
}
