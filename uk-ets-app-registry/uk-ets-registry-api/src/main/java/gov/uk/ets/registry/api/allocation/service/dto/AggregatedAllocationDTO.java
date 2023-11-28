package gov.uk.ets.registry.api.allocation.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class AggregatedAllocationDTO {
    /**
     * The annual allocations
     */
    private List<AnnualAllocationDTO> annuals;
    /**
     * The totals of the annual allocations
     */
    private TotalAllocationDTO totals;
}
