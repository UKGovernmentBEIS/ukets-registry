package gov.uk.ets.registry.api.user.profile.web;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class EmergencyOtpChangeRequest {
    @Email
    @NotEmpty
    private String email;
}
