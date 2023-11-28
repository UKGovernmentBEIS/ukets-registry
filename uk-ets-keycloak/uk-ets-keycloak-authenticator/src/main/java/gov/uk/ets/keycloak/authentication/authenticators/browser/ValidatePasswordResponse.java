package gov.uk.ets.keycloak.authentication.authenticators.browser;

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

	@Override
	public String toString() {
		return "ValidatePasswordResponse [valid=" + valid + ", errorCode=" + errorCode + ", message=" + message + "]";
	}
	
	

}
