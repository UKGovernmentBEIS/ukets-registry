package gov.uk.ets.registry.api.account.web.model.accountcontact;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContactInvitationStatusDTO {
	
    @EqualsAndHashCode.Include
    @NotNull
    private String email;
    
    @NotNull
    private Boolean invitationStatus;
    
}
