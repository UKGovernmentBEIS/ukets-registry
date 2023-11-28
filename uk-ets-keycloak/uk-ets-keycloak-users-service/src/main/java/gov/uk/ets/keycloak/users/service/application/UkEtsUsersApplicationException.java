package gov.uk.ets.keycloak.users.service.application;

/**
 * The Application exception type
 */
public class UkEtsUsersApplicationException extends Exception {
    public UkEtsUsersApplicationException(Throwable throwable) {
        super(throwable);
    }

    public UkEtsUsersApplicationException(String message) {
        super(message);
    }
}
