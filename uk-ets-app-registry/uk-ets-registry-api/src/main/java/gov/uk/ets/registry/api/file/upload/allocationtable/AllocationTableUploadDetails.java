package gov.uk.ets.registry.api.file.upload.allocationtable;

import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Transfer object for an allocation table upload.
 */
@Setter
@Getter
@ToString
public class AllocationTableUploadDetails {

    /**
     * The allocation category.
     */
    private AllocationCategory allocationCategory;
}
