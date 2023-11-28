package gov.uk.ets.registry.api.allocation.service.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The annual allocation DTO
 */
@Getter
@Builder
@EqualsAndHashCode
public class AnnualAllocationDTO {
    /**
     * The year
     */
    private Integer year;
    /**
     * The entitlement allowances
     */
    private Long entitlement;
    /**
     * The allocated allowances
     */
    private Long allocated;
    /**
     * The remaining allowances
     */
    private Long remaining;
    /**
     * The allocation status
     */
    private String status;

    private boolean eligibleForReturn;

    private Boolean excluded;
}
