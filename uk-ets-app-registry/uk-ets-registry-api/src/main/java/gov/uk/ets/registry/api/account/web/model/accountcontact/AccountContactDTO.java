package gov.uk.ets.registry.api.account.web.model.accountcontact;

import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AccountContactDTO {

    private String fullName;

    private String email;

    private PhoneNumberDTO phoneNumber;

    private LocalDateTime invitedOn;
}
