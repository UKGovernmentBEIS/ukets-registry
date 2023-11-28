package gov.uk.ets.registry.api.user.admin.shared;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

/**
 * Enrolled user data transfer object.
 */
@Builder
@Getter
public class EnrolledUserDTO implements Serializable {
    /**
     * User's first name
     */
    private String firstName;
    /**
     * User's last name
     */
    private String lastName;
    /**
     * User's unique business identifier
     */
    private String userId;
    /**
     * User's email
     */
    private String email;
    /**
     * User's known as name
     */
    private String knownAs;
}
