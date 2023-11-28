package gov.uk.ets.registry.api.reconciliation.repository;

import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationFailedEntry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for failed reconciliation entries.
 */
public interface ReconciliationFailedEntryRepository extends JpaRepository<ReconciliationFailedEntry, Long> {
    // nothing to implement here.
}
