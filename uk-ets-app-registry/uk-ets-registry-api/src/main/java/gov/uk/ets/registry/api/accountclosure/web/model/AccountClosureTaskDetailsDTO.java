package gov.uk.ets.registry.api.accountclosure.web.model;

import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountClosureTaskDetailsDTO extends TaskDetailsDTO {

    private AccountDetailsDTO accountDetails;
    private String closureComment;
    private String permitId;
    private String monitoringPlanId;
    private boolean noActiveAR;
    private boolean pendingAllocationTaskExists;
    private AllocationClassification allocationClassification;

    public AccountClosureTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
