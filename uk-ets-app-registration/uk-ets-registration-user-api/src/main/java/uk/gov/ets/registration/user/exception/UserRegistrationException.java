package uk.gov.ets.registration.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates any error during the UserRegistration process not covered by other exceptions.
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserRegistrationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	final int status;

	public UserRegistrationException(int status) {
		super();
		this.status = status;
	}

	@Override
	public String getMessage() {
		return String.format("Keycloak server return HttpStatus: %s", HttpStatus.valueOf(status));
	}
	

}
