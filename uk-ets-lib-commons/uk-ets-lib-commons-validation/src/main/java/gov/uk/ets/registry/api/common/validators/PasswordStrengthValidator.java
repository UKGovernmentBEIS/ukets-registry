package gov.uk.ets.registry.api.common.validators;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

	private int minimumStrength;

	@Override
	public void initialize(PasswordStrength constraint) {
		minimumStrength = constraint.min();
	}

	@Override
	public boolean isValid(String passwd, ConstraintValidatorContext context) {

		if (Objects.isNull(passwd)) {
			return false;
		}

		String truncatedPassword = passwd.substring(0, Math.min(
			passwd.length(),
			Integer.parseInt(System.getenv().getOrDefault("PASSWORD_MAX_CHARS", "128"))));

		Zxcvbn zxcvbn = new Zxcvbn();
		Strength strength = zxcvbn.measure(truncatedPassword);

		HibernateConstraintValidatorContext hibernateContext = context
				.unwrap(HibernateConstraintValidatorContext.class);
		hibernateContext.addMessageParameter("score", strength.getScore());

		return strength.getScore() > minimumStrength;
	}

}
