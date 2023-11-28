package gov.uk.ets.keycloak.users.service.application.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The uk-ets user projection
 */
@Getter
@EqualsAndHashCode
public class UserProjection {
    /**
     * The urid user attribute
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
     * The state user attribute
     */
    private String status;
    /**
     * The last sign in user attribute
     */
    private String lastSignInDate;
    /**
     * The user registration date
     */
    private String registeredOnDate;
    /**
     * The user known as name
     */
    private String knownAs;

    @QueryProjection
    public UserProjection(String userId,
        String firstName,
        String lastName,
        String status,
        String lastSignInDate,
        String registeredOnDate,
        String knownAs) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.lastSignInDate = lastSignInDate;
        this.registeredOnDate = registeredOnDate;
        this.knownAs = knownAs;
    }

    @Override
    public String toString() {
        return "UserProjection{" +
            "userId='" + userId + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", status='" + status + '\'' +
            ", lastSignInDate='" + lastSignInDate + '\'' +
            ", registeredOnDate='" + registeredOnDate + '\'' +
            ", knownAs='" + knownAs + '\'' +
            '}';
    }
}
