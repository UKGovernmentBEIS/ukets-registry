/**
 * 
 */
package uk.gov.ets.registration.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that the verification link has expired.
 * @author P35036
 */
@ResponseStatus(HttpStatus.GONE)
public class EmailVerificationExpiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return String.format("The verification link has expired.");
	}

	
	
}
