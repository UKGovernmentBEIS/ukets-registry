package gov.uk.ets.registry.api.common.validators;

public class PasswordStrengthValidatorTestDTO {

	@PasswordStrength
	String newPasswd;

	public PasswordStrengthValidatorTestDTO(String newPasswd) {
		super();
		this.newPasswd = newPasswd;
	}

	public String getNewPasswd() {
		return newPasswd;
	}

}
