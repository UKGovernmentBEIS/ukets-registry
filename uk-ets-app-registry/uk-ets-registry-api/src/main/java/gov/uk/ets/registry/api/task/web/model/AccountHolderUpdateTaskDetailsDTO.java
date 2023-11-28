package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderInAccountsDTO;
import java.util.List;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHolderUpdateTaskDetailsDTO extends TaskDetailsDTO {

    private AccountDetailsDTO accountDetails;
    private List<AccountHolderInAccountsDTO> accountHolderOwnership;
    private AccountHolderDTO accountHolder;
    private AccountHolderDetailsUpdateDiffDTO accountHolderDiff;
    private AccountHolderRepresentativeDTO accountHolderContact;
    private AccountHolderRepresentativeDTO accountHolderContactDiff;

    public AccountHolderUpdateTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
