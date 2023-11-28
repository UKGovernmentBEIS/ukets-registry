package gov.uk.ets.registry.api.accounttransfer.web.model;

import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransferTaskDetailsDTO extends TaskDetailsDTO {

    private AccountTransferAction action;
    private AccountHolderDTO currentAccountHolder;
    private AccountDetailsDTO account;

    public AccountTransferTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
