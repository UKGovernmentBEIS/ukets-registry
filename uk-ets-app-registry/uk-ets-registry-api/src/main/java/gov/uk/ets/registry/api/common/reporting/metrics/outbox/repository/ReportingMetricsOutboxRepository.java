package gov.uk.ets.registry.api.common.reporting.metrics.outbox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.registry.api.common.reporting.metrics.domain.ReportingMetricsOutbox;
import gov.uk.ets.registry.api.common.reporting.metrics.types.OutboxStatus;

public interface ReportingMetricsOutboxRepository extends JpaRepository<ReportingMetricsOutbox, Long> {

    List<ReportingMetricsOutbox> findByStatusOrderByGeneratedOnAsc(OutboxStatus status);
}
