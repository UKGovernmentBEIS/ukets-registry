package gov.uk.ets.registry.api.allocation.service.dto;

import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class UpdateAllocationCommand {
    /**
     * The account identifier
     */
    private Long accountId;
    /**
     * The map with key the year and value the changed status
     */
    private Map<Integer, AllocationStatusType> changedStatus;
    /**
     * The justification user comment.
     */
    private String justification;
}
