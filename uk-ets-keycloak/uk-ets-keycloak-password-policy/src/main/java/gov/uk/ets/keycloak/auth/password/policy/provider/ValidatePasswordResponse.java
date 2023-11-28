package gov.uk.ets.keycloak.auth.password.policy.provider;

public class ValidatePasswordResponse {

	private boolean valid = false;
	private String errorCode;
	private String message;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
