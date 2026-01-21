package gov.uk.ets.registry.api.account.web.model.accountcontact;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactSendInvitationDTO {

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@Valid MetsContactDTO> metsContacts = new HashSet<>();

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@Valid RegistryContactDTO> registryContacts = new HashSet<>();
}
