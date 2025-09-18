package gov.uk.ets.registry.api.accountholder.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class AccountHolderChangeDTO {

    @NotNull
    private Long accountIdentifier;

    @NotNull
    private AccountHolderDTO acquiringAccountHolder;

    @NotNull
    private AccountHolderRepresentativeDTO acquiringAccountHolderContactInfo;
}
