package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for allocation jobs.
 */
public interface AllocationJobRepository extends JpaRepository<AllocationJob, Long> {

    /**
     * Retrieves the allocation job with the provided status.
     *
     * @param status The status.
     * @return list of allocation jobs.
     */
    List<AllocationJob> findByStatus(AllocationJobStatus status);

    List<AllocationJob> findByStatusIn(List<AllocationJobStatus> statuses);

    List<AllocationJob> findByCategoryAndStatusIn(AllocationCategory category, List<AllocationJobStatus> statuses);

    AllocationJob findByCategoryAndYearAndStatusIn(AllocationCategory category, Integer year, List<AllocationJobStatus> statuses);


}
