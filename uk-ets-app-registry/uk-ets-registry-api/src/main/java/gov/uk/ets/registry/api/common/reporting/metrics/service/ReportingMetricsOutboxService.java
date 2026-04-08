package gov.uk.ets.registry.api.common.reporting.metrics.service;

import gov.uk.ets.registry.api.common.reporting.metrics.domain.ReportingMetricsOutbox;
import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import gov.uk.ets.registry.api.common.reporting.metrics.outbox.repository.ReportingMetricsOutboxRepository;
import gov.uk.ets.registry.api.common.reporting.metrics.types.OutboxStatus;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service is responsible for processing the reporting metrics events.
 * It is the separate process that implements the outbox pattern for kafka messages and db operations.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ReportingMetricsOutboxService {

    private final ReportingMetricsOutboxRepository repository;
    private final ReportingMetricsEventProducer producer;
    
    
    /**
     * Creates an outbox entry with status PENDING.
     * The payload contains the event data that will be sent from the system.
     */
    @Transactional
    public void create(AbstractReportingMetricsEvent event) {
        ReportingMetricsOutbox outboxEvent = ReportingMetricsOutbox.builder()
            .generatedOn(event.getDateTriggered())
            .status(OutboxStatus.PENDING)
            .eventId(event.getEventId())
            .payload(event)
            .build();

        repository.save(outboxEvent);
    }    
    
    /**
     * Retrieves all outbox entries with status PENDING.
     * Sends the message.
     * Updates the outbox entry to status SENT.
     */
    @Transactional
    public void processEvents() {
        List<ReportingMetricsOutbox> outboxEntries =
            repository.findByStatusOrderByGeneratedOnAsc(OutboxStatus.PENDING);

        outboxEntries.forEach(entry -> {
            AbstractReportingMetricsEvent event = entry.getPayload();
            log.info("Sending reporting metric event {}", event);
            //send event
            producer.send(event);
            // and update status
            entry.setStatus(OutboxStatus.SENT);
        });
    }

}
