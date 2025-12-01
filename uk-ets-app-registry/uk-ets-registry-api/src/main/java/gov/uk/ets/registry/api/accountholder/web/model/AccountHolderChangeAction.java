package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class AccountHolderChangeAction {

    private AccountHolderChangeActionType type;
    private AccountHolderDTO accountHolderDTO;
    private AccountHolderRepresentativeDTO accountHolderContactInfo;
    private Boolean accountHolderDelete;
}
