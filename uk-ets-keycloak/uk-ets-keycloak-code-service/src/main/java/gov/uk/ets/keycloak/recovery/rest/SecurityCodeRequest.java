package gov.uk.ets.keycloak.recovery.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityCodeRequest {

    private String userId;
    private String email;
    private String countryCode;
    private String phoneNumber;

}
