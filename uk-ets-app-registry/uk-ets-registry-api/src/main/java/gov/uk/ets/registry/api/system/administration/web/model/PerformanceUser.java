package gov.uk.ets.registry.api.system.administration.web.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceUser {

    /**
     * The keycloak id.
     */
    private String keycloakId;

    /**
     * The username.
     */
    private String username;

    /**
     * The password.
     */
    private String password;

    /**
     * The secret for OTP.
     */
    private String secret;

}
