package gov.uk.ets.registry.api.user.profile.recovery.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryMethodUpdateResponse {

    private String email;
    private String countryCode;
    private String phoneNumber;
    private Long expiresInMillis;
}
