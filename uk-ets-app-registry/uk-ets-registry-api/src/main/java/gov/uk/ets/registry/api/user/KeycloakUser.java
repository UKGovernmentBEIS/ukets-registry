package gov.uk.ets.registry.api.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.idm.UserRepresentation;

@Getter
@Setter
@NoArgsConstructor
public class KeycloakUser {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean eligibleForSpecificActions;
    private Map<String, List<String>> attributes;
    private Set<String> userRoles;
    /**
     *  The KeycloakUser constructor.
     * @param userRepresentation the UserRepresentation object.
     */
    public KeycloakUser(UserRepresentation userRepresentation) {
        this.username = userRepresentation.getUsername();
        this.email = userRepresentation.getEmail();
        this.firstName = userRepresentation.getFirstName();
        this.lastName = userRepresentation.getLastName();
        this.attributes = userRepresentation.getAttributes();
    }
}
