/**
 * 
 */
package uk.gov.ets.registration.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Holds registration parameters
 *
 */
public class UserRegisterParams {
	
	@NotBlank(message="Email cannot be empty.")
	@Email(message="Email must be valid.")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
