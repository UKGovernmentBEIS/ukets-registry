package gov.uk.ets.registry.api.account.web.model.accountcontact;

import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AccountContactDTO {

    @EqualsAndHashCode.Include
    @NotBlank
    private String fullName;

    @EqualsAndHashCode.Include
    @NotBlank
    private String email;

    @Valid
    private PhoneNumberDTO phoneNumber;

    private LocalDateTime invitedOn;
}
