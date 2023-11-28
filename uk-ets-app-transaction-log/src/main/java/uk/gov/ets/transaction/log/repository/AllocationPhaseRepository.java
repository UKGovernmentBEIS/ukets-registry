package uk.gov.ets.transaction.log.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.ets.transaction.log.domain.AllocationPhase;

/**
 * Repository for allocation phases.
 */
public interface AllocationPhaseRepository extends JpaRepository<AllocationPhase, Long> {

    /**
     * Retrieves the allocation phase based on the provided year.
     * @param year The year.
     * @return an allocation phase.
     */
    @Query(
        "select ph " +
        "  from AllocationPhase ph " +
        " where ph.firstYear <= ?1 " +
        "   and ph.lastYear >= ?1  ")
    Optional<AllocationPhase> getAllocationPhaseBasedOnYear(Integer year);

}
