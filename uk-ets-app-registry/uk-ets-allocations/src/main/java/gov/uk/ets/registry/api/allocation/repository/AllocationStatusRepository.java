package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for allocation statuses.
 */
public interface AllocationStatusRepository extends JpaRepository<AllocationStatus, Long> {

    /**
     * Retrieves the allocation status of a compliant entity (Installation, Aircraft Operator),
     * for the provided year.
     * @param compliantEntityId The compliant entity id.
     * @param year The allocation year.
     * @return the allocation status.
     */
    @SuppressWarnings("java:S100")
    AllocationStatus findByCompliantEntityIdAndAllocationYear_Year(Long compliantEntityId, Integer year);

}
