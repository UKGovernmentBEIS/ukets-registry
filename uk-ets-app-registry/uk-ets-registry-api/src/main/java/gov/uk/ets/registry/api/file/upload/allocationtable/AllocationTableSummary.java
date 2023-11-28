package gov.uk.ets.registry.api.file.upload.allocationtable;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Transfer object for an allocation table.
 */
@Setter
@Getter
@ToString
public class AllocationTableSummary implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -7197603442208175412L;

    /**
     * The allocation type.
     */
    AllocationType type;

    /**
     * The allocation entries.
     */
    private List<AllocationSummary> allocations;

}
