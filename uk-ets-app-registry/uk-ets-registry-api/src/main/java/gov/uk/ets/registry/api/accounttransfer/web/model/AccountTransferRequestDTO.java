package gov.uk.ets.registry.api.accounttransfer.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountTransferRequestDTO {

    @MDCParam(ACCOUNT_ID)
    private Long accountIdentifier;
    private AccountTransferActionType accountTransferType;
    private Long existingAcquiringAccountHolderIdentifier;
    private AccountHolderDTO acquiringAccountHolder;
    private AccountHolderRepresentativeDTO acquiringAccountHolderContactInfo;
}
