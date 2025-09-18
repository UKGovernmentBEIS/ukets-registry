package gov.uk.ets.registry.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a user transfer object.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDTO implements Serializable {
    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -3825788239425305246L;

    /**
     * Constructor.
     */
    public UserDTO() {
        // nothing to implement here
    }

    /**
     * Constructor.
     * @param urid The URID.
     * @param firstName The first name.
     * @param lastName The last name.
     */
    public UserDTO(String urid, String firstName, String lastName) {
        this.urid = urid;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Constructor.
     * @param urid The URID.
     * @param firstName The first name.
     * @param lastName The last name.
     * @param alsoKnownAs The known as name.
     */
    public UserDTO(String urid, String firstName, String lastName, String alsoKnownAs) {
        this.urid = urid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.alsoKnownAs = alsoKnownAs;
    }

    /**
     * Constructor.
     * @param urid The URID.
     * @param status The user status.
     * @param firstName The first name.
     * @param lastName The last name.
     */
    public UserDTO(String urid, UserStatus status, String firstName, String lastName, String knownAs) {
        this.urid = urid;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.alsoKnownAs = knownAs;
    }

    /**
     * The URID.
     */
    private String urid;

    /**
     * The unique identifier in Keycloak.
     */
    @JsonProperty("userId")
    private String keycloakId;

    /**
     * The status.
     */
    private UserStatus status;

    /**
     * The first name.
     */
    @Size(max = 100, message = "First name must not exceed 100 characters.")
    private String firstName;

    /**
     * The last name.
     */
    @Size(max = 100, message = "Last name must not exceed 100 characters.")
    private String lastName;

    /**
     * The known as name.
     */
    @Size(max = 100, message = "Known as name must not exceed 100 characters.")
    private String alsoKnownAs;

    @Email
    private String email;
}
