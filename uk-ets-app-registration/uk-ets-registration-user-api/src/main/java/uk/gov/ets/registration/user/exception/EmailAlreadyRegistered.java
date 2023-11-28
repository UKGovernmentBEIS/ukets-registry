package uk.gov.ets.registration.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that the email is already registered in the keycloak server.
 *
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class EmailAlreadyRegistered extends RuntimeException {

	private static final long serialVersionUID = 1L;
    private String email;

    public EmailAlreadyRegistered(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("Email %s is already registered.", email);
    }

}
