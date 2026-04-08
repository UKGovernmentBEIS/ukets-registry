package gov.uk.ets.registry.api.common.reporting.metrics.messaging.config;

import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

@Configuration
@RequiredArgsConstructor
public class ReportingMetricsMessagingConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.reporting.metrics.consumer.group.id:group.reporting.metrics.consumer.group}")
    private String reportingMetricsConsumerGroupId;

    @Value("${kafka.reporting.metrics.consumer.json.trusted.packages:*}")
    private String reportingMetricsTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaProducerConfig producerConfig;

    @Bean("reportingMetricsKafkaTemplate")
    KafkaTemplate<String, Serializable> reportingMetricsKafkaTemplate() {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uk-ets-reporting-metrics")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }
}
