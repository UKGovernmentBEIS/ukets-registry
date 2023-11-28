package gov.uk.ets.registry.api.accounttransfer.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class AccountTransferAction {
    private AccountTransferActionType type;
    private AccountHolderDTO accountHolderDTO;
    private AccountHolderRepresentativeDTO accountHolderContactInfo;
    private AccountStatus previousAccountStatus;
    private InstallationOrAircraftOperatorDTO installationDetails;
}

