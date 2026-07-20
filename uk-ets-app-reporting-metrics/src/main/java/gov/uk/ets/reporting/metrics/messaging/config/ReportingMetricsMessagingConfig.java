package gov.uk.ets.reporting.metrics.messaging.config;

import gov.uk.ets.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class ReportingMetricsMessagingConfig {

    @Value("${kafka.reporting.metrics.consumer.group.id:reporting-metrics-consumer-registry-group}")
    private String reportingMetricsConsumerGroupId;
    
    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("reportingMetricsListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, AbstractReportingMetricsEvent> reportingMetricsListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AbstractReportingMetricsEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(AbstractReportingMetricsEvent.class, reportingMetricsConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
