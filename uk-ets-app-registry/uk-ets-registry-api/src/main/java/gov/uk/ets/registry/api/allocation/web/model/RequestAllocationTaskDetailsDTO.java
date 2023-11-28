package gov.uk.ets.registry.api.allocation.web.model;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAllocationTaskDetailsDTO extends TaskDetailsDTO {

    AllocationOverview allocationOverview;
    String natAccountName;
    String nerAccountName;
    Long currentHoldings;
    Long nerCurrentHoldings;

    public RequestAllocationTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
