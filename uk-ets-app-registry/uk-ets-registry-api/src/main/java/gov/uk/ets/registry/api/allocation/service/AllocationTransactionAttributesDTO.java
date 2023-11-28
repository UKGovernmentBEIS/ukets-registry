package gov.uk.ets.registry.api.allocation.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllocationTransactionAttributesDTO {

    private final Integer allocationYear;
    private final String allocationType;
}
