package gov.uk.ets.reporting.metrics.outbox.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.reporting.metrics.domain.ProcessedAccountMetricsEvent;

public interface ProcessedAccountMetricsEventRepository extends JpaRepository<ProcessedAccountMetricsEvent, Long> {
        
    Optional<ProcessedAccountMetricsEvent> findByEventId(UUID eventId);
}
