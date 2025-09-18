package gov.uk.ets.registry.api.user.profile.recovery.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryMethodRemoveRequest {

    private Boolean removeEmail;
    private Boolean removePhoneNumber;
    private String otpCode;
}
