package gov.uk.ets.registry.api.integration.changelog.repository;

import gov.uk.ets.registry.api.integration.changelog.domain.IntegrationChangeLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Integration change log.
 */
public interface IntegrationChangeLogRepository extends JpaRepository<IntegrationChangeLog, Long> {

    Optional<IntegrationChangeLog> findTopByOperatorIdAndEntityOrderByUpdatedAtDesc(Long operatorIdentifier, String entity);
}
