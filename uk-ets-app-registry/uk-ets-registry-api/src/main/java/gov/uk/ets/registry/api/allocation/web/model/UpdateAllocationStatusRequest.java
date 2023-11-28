package gov.uk.ets.registry.api.allocation.web.model;

import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAllocationStatusRequest {
    @NotEmpty
    private Map<Integer, AllocationStatusType> changedStatus;
    @NotBlank
    private String justification;
}
