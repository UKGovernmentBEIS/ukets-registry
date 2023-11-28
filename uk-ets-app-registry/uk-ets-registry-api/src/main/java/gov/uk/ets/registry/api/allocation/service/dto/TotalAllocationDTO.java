package gov.uk.ets.registry.api.allocation.service.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The total allocation of the account
 */
@Getter
@Builder
@EqualsAndHashCode
public class TotalAllocationDTO {

    /**
     * The total entitlement allowances
     */
    private Long entitlement;
    /**
     * The total allocated allowances
     */
    private Long allocated;
    /**
     * The remaining allowances
     */
    private Long remaining;
}
