package gov.uk.ets.registry.api.account.web.model.accountcontact;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactChangeInvitationStatusDTO {

    @Builder.Default
    @Valid
    private List<ContactInvitationStatusDTO> metsContacts = new ArrayList<>();

    @Builder.Default
    @Valid
    private List<RegistryContactInvitationStatusDTO> registryContacts = new ArrayList<>();
}
