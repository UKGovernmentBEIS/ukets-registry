package gov.uk.ets.registry.api.common.reporting.metrics.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

import java.io.Serializable;

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class ReportingMetricsMessagingConfig {

    @Value("${kafka.ets.registry.reporting.metrics.event.transactional.id}")
    private String reportingMetricsTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("reportingMetricsKafkaTemplate")
    KafkaTemplate<String, Serializable> reportingMetricsKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(reportingMetricsTransactionalId, null);
    }
}
