package uk.gov.ets.transaction.log.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.ets.transaction.log.domain.Reconciliation;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {

    /**
     * Retrieves the latest reconciliation date (completed or inconsistent).
     */
    @Query(" select max(created) from Reconciliation " +
        "where status = uk.gov.ets.transaction.log.domain.type.ReconciliationStatus.COMPLETED")
    Date findLatestReconciliationDate();
}
