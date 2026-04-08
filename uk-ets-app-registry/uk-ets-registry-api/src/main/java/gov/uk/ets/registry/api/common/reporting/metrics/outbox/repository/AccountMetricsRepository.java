package gov.uk.ets.registry.api.common.reporting.metrics.outbox.repository;

import gov.uk.ets.registry.api.common.reporting.metrics.domain.AccountMetrics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMetricsRepository extends JpaRepository<AccountMetrics, Long> {

    Optional<AccountMetrics> findByIdentifier(Long identifier);
}
