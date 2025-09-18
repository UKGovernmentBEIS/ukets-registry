package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationJobError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationJobErrorRepository extends JpaRepository<AllocationJobError, Long> {
}
