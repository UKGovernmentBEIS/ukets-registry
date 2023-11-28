package gov.uk.ets.registry.api.reconciliation.repository;

    import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationHistory;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;

/**
 * Repository for reconciliation history.
 */
public interface ReconciliationHistoryRepository extends JpaRepository<ReconciliationHistory, Long> {

    /**
     * Removes the history entries of a reconciliation.
     * @param reconciliationIdentifier The reconciliation identifier
     */
    @Modifying
    @Query("delete from ReconciliationHistory h where h.reconciliation in (select r from Reconciliation r where r.identifier = ?1)")
    void deleteAllByReconciliationIdentifier(Long reconciliationIdentifier);
}
