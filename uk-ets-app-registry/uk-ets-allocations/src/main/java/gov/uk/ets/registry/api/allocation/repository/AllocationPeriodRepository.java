package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for allocation periods.
 */
public interface AllocationPeriodRepository extends JpaRepository<AllocationPeriod, Long> {
    // nothing to implement at the moment.
}
