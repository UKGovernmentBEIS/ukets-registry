package gov.uk.ets.registry.api.user.admin.shared;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * The projection of keycloak user data transfer object
 */
@Getter
@Setter
public class KeycloakUserProjection implements Serializable {
    /**
     * The user iam id
     */
    private String userId;
    /**
     * The user's first name
     */
    private String firstName;
    /**
     * The user's last name
     */
    private String lastName;
    /**
     * The user's status
     */
    private String status;
    /**
     * The user's last sign in date
     */
    private String lastSignInDate;
    /**
     * The user's registration date
     */
    private String registeredOnDate;
    /**
     * The user's known as name
     */
    private String knownAs;
}
