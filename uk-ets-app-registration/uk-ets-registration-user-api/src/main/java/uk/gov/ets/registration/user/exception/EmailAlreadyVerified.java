package uk.gov.ets.registration.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that the email is already verified by the user.
 *
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyVerified extends RuntimeException {

	private static final long serialVersionUID = 1L;
    private String email;

    public EmailAlreadyVerified(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("Email %s is already confirmed.", email);
    }

}
