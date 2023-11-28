package gov.uk.ets.registry.api.allocation;

import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationPeriod;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import org.springframework.beans.factory.annotation.Autowired;

public class ParentAllocationBase {

    @Autowired
    protected AllocationYearRepository allocationYearRepository;
    protected AllocationEntry createAllocationEntry(Long aircraftId, Integer year, Long entitlement, Long allocated, AllocationType type) {
        return createAllocationEntry(aircraftId, year, entitlement, allocated, type, 0L, 0L);
    }

    protected AllocationEntry createAllocationEntry(Long aircraftId, Integer year, Long entitlement, Long allocated,
                                                    AllocationType type, Long returned, Long reversed) {
        AllocationEntry entry = new AllocationEntry();
        entry.setAllocationYear(allocationYearRepository.findByYear(year));
        entry.setEntitlement(entitlement);
        entry.setAllocated(allocated);
        entry.setReturned(returned);
        entry.setReversed(reversed);
        entry.setCompliantEntityId(aircraftId);
        entry.setType(type);
        return entry;
    }

    protected AllocationStatus createAllocationStatus(Long aircraftId, Integer year, AllocationStatusType status) {
        AllocationStatus result = new AllocationStatus();
        result.setAllocationYear(allocationYearRepository.findByYear(year));
        result.setCompliantEntityId(aircraftId);
        result.setStatus(status);
        return result;
    }

    protected AllocationYear createYear(AllocationPeriod allocationPeriod, Integer year) {
        AllocationYear result = new AllocationYear();
        result.setYear(year);
        result.setPeriod(allocationPeriod);
        result.setAllocatedYearly(0L);
        result.setAuctionedYearly(0L);
        result.setConsumedYearlyCap(0L);
        result.setEntitlementYearly(0L);
        result.setInitialYearlyCap(100000L);
        result.setPendingYearlyCap(0L);
        result.setReturnedYearly(0L);
        result.setReversedYearly(0L);
        return result;
    }
    
    protected ExcludeEmissionsEntry createExcludeEmissionsEntry(Long compliantEntityIdentifier, Long year, boolean excluded) {
        ExcludeEmissionsEntry result = new ExcludeEmissionsEntry();
        result.setCompliantEntityId(compliantEntityIdentifier);
        result.setYear(year);
        result.setExcluded(excluded);
        return result;
    }
    
    protected CompliantEntity createCompliantEntity(Long compliantEntityIdentifier, Integer startYear, Integer endYear) {
        CompliantEntity result = new CompliantEntity();
        result.setIdentifier(compliantEntityIdentifier);
        result.setStartYear(startYear);
        result.setEndYear(endYear);
        return result;
    }
}
