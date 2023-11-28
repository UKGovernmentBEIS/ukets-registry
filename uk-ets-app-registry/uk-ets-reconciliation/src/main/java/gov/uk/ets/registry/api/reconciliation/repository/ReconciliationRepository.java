package gov.uk.ets.registry.api.reconciliation.repository;


import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for reconciliations.
 */
public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long>, ReconciliationCustomRepository {

    /**
     * Retrieves the next reconciliation business identifier.
     *
     * @return the next reconciliation business identifier.
     */
    @Query(value = "select nextval('reconciliation_identifier_seq')", nativeQuery = true)
    Long getNextIdentifier();

    /**
     * Retrieves the first reconciliation with one of the provided statuses.
     * @param statuses The statuses.
     * @return a reconciliation.
     */
    Optional<Reconciliation> findFirstByStatusIn(List<ReconciliationStatus> statuses);

    /**
     * Retrieves the reconciliations of status
      * @param status The status
     * @return THe reconciliations
     */
    List<Reconciliation> findByStatus(ReconciliationStatus status);

    /**
     * Retrieves reconciliation by unique business identifier.
     * @param identifier
     * @return
     */
    Reconciliation findByIdentifier(Long identifier);

    /**
     * Deletes the reconciliation by identifier.
     * @param identifier The identifier of reconciliation to be deleted.
     */
    void removeByIdentifier(Long identifier);
}
