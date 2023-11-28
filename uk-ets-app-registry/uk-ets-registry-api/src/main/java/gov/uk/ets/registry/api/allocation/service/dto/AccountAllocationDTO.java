package gov.uk.ets.registry.api.allocation.service.dto;

import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class AccountAllocationDTO {
    private AggregatedAllocationDTO standard;
    private AggregatedAllocationDTO underNewEntrantsReserve;
    private AllocationClassification allocationClassification;
}
