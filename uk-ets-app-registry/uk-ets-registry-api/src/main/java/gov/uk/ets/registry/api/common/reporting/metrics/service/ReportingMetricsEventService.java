package gov.uk.ets.registry.api.common.reporting.metrics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;

@Service
@RequiredArgsConstructor
public class ReportingMetricsEventService {

    private final ReportingMetricsOutboxService service;
    
    /**
     * Processing the events consists of persisting it to the outbox table.
     */
    public void processEvent(AbstractReportingMetricsEvent event) {
        service.create(event);
    }
    
}
