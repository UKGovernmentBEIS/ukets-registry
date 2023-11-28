package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AllocationStatusService {

    /**
     * Repository for allocation entries.
     */
    private AllocationEntryRepository allocationEntryRepository;

    /**
     * Repository for allocation status.
     */
    private AllocationStatusRepository allocationStatusRepository;

    /**
     * Retrieves the allocation entries for the provided installation / aircraft operator.
     * @param compliantEntityId The installation / aircraft operator id.
     * @param type The allocation type.
     * @return some allocation summaries
     */
    public List<AllocationSummary> getAllocationEntries(Long compliantEntityId, AllocationType type) {
        return allocationEntryRepository.retrieveAllocationEntries(compliantEntityId, type);
    }

    /**
     * Retrieves the allocation entries for the provided installation / aircraft operator.
     * @param compliantEntityId The installation / aircraft operator id.
     * @return some allocation summaries
     */
    public List<AllocationSummary> getAllocationEntries(Long compliantEntityId) {
        return allocationEntryRepository.retrieveAllocationEntries(compliantEntityId);
    }

    /**
     * Retrieves the account allocation status per year.
     * @param compliantEntityId The installation / aircraft operator id.
     * @return some allocation summaries
     */
    public List<AllocationSummary> getAllocationStatus(Long compliantEntityId) {
        return allocationEntryRepository.retrieveAllocationStatus(compliantEntityId);
    }

    /**
     * Updates the allocation status.
     * @param compliantEntityId The installation / aircraft operator id.
     * @param statusMap The new status map.
     */
    public void updateAllocationStatus(Long compliantEntityId, Map<Integer, AllocationStatusType> statusMap) {
        statusMap.entrySet().stream().forEach(entry -> {
            AllocationStatus status = allocationStatusRepository.findByCompliantEntityIdAndAllocationYear_Year(compliantEntityId, entry.getKey());
            status.setStatus(entry.getValue());
            allocationStatusRepository.save(status);
        });
    }

}
