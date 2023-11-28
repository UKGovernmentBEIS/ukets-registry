package gov.uk.ets.reports.generator.domain;

import java.security.InvalidParameterException;
import java.util.Arrays;
import lombok.Getter;

/**
 * Enumerates the various user roles.
 */
@Getter
public enum UserRole {

    /**
     * Verifier.
     */
    VERIFIER("verifier"),

    /**
     * Authorised representative.
     */
    AUTHORISED_REPRESENTATIVE("authorized-representative"),

    /**
     * Junior registry administrator.
     */
    JUNIOR_REGISTRY_ADMINISTRATOR("junior-registry-administrator"),

    /**
     * Read-only administrator.
     */
    READONLY_ADMINISTRATOR("readonly-administrator"),

    /**
     * Senior registry administrator.
     */
    SENIOR_REGISTRY_ADMINISTRATOR("senior-registry-administrator"),

    /**
     * System administrator.
     */
    SYSTEM_ADMINISTRATOR("system-administrator"),

    /**
     * Authority user.
     */
    AUTHORITY_USER("authority-user");

    private String keycloakLiteral;

    UserRole(String keycloakLiteral) {
        this.keycloakLiteral = keycloakLiteral;
    }

    /**
     * Getss a user role from a string keycloak literal.
     *
     * @param keycloakLiteral the input literal
     * @return a user role
     */
    public static UserRole fromKeycloakLiteral(String keycloakLiteral) {
        return Arrays.stream(values())
            .filter(userRole -> userRole.keycloakLiteral.equals(keycloakLiteral))
            .findFirst()
            .orElseThrow(InvalidParameterException::new);
    }

    /**
     * Checks for senior/junior registry administrator.
     *
     * @return true if user is read-write registry administrator false otherwise
     */
    public boolean isSeniorOrJuniorRegistryAdministrator() {
        return this.equals(JUNIOR_REGISTRY_ADMINISTRATOR) || this.equals(SENIOR_REGISTRY_ADMINISTRATOR);
    }

    /**
     * Checks for read-only/junior/senior registry administrator.
     *
     * @return true if user is an administrator false otherwise
     */
    public boolean isRegistryAdministrator() {
        return this.equals(JUNIOR_REGISTRY_ADMINISTRATOR) || this.equals(SENIOR_REGISTRY_ADMINISTRATOR) ||
            this.equals(READONLY_ADMINISTRATOR);
    }

    /**
     * Checks for readonly administrator.
     *
     * @return true if user is readonly administrator false otherwise
     */
    public boolean isReadOnlyAdministrator() {
        return this.equals(READONLY_ADMINISTRATOR);
    }

    /**
     * Checks for junior administrator.
     *
     * @return true if user is junior administrator false otherwise
     */
    public boolean isJuniorAdministrator() {
        return this.equals(JUNIOR_REGISTRY_ADMINISTRATOR);
    }


    /**
     * Checks for senior administrator.
     *
     * @return true if user is administrator false otherwise
     */
    public boolean isSeniorRegistryAdministrator() {
        return this.equals(SENIOR_REGISTRY_ADMINISTRATOR);
    }

    /**
     * Checks for authorized representative.
     *
     * @return true if user is authorised representative false otherwise
     */
    public boolean isAuthorizedRepresentative() {
        return this.equals(AUTHORISED_REPRESENTATIVE);
    }

    /**
     * Checks for admin or authority user.
     *
     * @return true if user is any administrator or authority user
     */
    public boolean isAdminOrAuthorityUser() {
        return this.equals(JUNIOR_REGISTRY_ADMINISTRATOR) ||
            this.equals(SENIOR_REGISTRY_ADMINISTRATOR) ||
            this.equals(READONLY_ADMINISTRATOR) ||
            this.equals(AUTHORITY_USER);
    }
}
