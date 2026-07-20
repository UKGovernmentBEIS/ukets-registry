package gov.uk.ets.registry.api.common.reporting.metrics.service;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;

/**
 * Class is intentionally package private, the only entry point to this producer is through
 * the {@link ReportingMetricsOutboxService}.
 */
@Log4j2
@Service
class ReportingMetricsEventProducer {

    private final String etsRegistryReportingMetricsEventTopic;

    private final KafkaTemplate<String, Serializable> reportingMetricsKafkaTemplate;

    public ReportingMetricsEventProducer(
        @Value("${kafka.ets.registry.reporting.metrics.event.topic:registry-internal-reporting-metrics-event-topic}") String etsRegistryReportingMetricsEventTopic,
        @Qualifier("reportingMetricsKafkaTemplate")
            KafkaTemplate<String, Serializable> reportingMetricsKafkaTemplate) {
        this.etsRegistryReportingMetricsEventTopic = etsRegistryReportingMetricsEventTopic;
        this.reportingMetricsKafkaTemplate = reportingMetricsKafkaTemplate;
    }

    /**
     * Partitions messages by compliantEntityId, so as to preserve the order of messages on the consumer side.
     */
    @Transactional
    public void send(AbstractReportingMetricsEvent event) {
        log.info("Sending a reporting metrics event {}", event);
        try {
            reportingMetricsKafkaTemplate.send(etsRegistryReportingMetricsEventTopic, 
                    String.valueOf(event.getIdentifier()), 
                    event)
                .get();
        } catch (Exception e) {
            log.error("reporting metrics event failed to be sent: {}", event, e);
            throw new RuntimeException(e);
        }
    }
    
}
