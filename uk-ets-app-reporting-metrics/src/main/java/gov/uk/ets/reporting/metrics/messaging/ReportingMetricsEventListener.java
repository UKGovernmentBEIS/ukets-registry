package gov.uk.ets.reporting.metrics.messaging;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import gov.uk.ets.reporting.metrics.service.AccountMetricsReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@KafkaListener(topics = "${kafka.ets.registry.reporting.metrics.event.topic:registry-internal-reporting-metrics-event-topic}",
    containerFactory = "reportingMetricsListenerContainerFactory")
@RequiredArgsConstructor
@Service
@Log4j2
public class ReportingMetricsEventListener {

    private final AccountMetricsReportingService accountMetricsReportingService;
    
    @KafkaHandler
    @Transactional
    public void processReportingMetricEvent(AbstractReportingMetricsEvent event) {
        log.info("Received ReportingMetricsEvent {}", event);
        accountMetricsReportingService.processAccountMetricEvent(event);
    }
    
}
