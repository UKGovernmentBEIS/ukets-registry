package gov.uk.ets.registry.api.account.web.model.accountcontact;

import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RegistryContactInvitationStatusDTO extends ContactInvitationStatusDTO {

    @NotNull
    private AccountContactType contactType;
}
