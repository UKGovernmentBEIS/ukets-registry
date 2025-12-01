package gov.uk.ets.registry.api.accountholder.web.model;

import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHolderChangeTaskDetailsDTO extends TaskDetailsDTO {

    private AccountHolderChangeAction action;
    private AccountHolderDTO currentAccountHolder;
    private AccountDetailsDTO account;

    public AccountHolderChangeTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
