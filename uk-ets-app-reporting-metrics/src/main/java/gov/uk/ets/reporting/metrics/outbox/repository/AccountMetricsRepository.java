package gov.uk.ets.reporting.metrics.outbox.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.reporting.metrics.domain.AccountMetrics;

public interface AccountMetricsRepository extends JpaRepository<AccountMetrics, Long> {

    Optional<AccountMetrics> findByIdentifier(Long identifier);
}
