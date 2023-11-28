package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
        "  join ph.periods pr      " +
        "  join pr.years yr        " +
        " where yr.year = ?1       ")
    AllocationPhase getAllocationPhaseOfYear(Integer year);

}
