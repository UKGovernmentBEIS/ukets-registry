package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountClosureDTO {

    private AccountDetailsDTO accountDetails;
    private String closureComment;
    private boolean noActiveAR;
    private AllocationClassification allocationClassification;
}
