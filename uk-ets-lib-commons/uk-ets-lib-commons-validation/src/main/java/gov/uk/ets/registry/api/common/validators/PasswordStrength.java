package gov.uk.ets.registry.api.common.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
public @interface PasswordStrength {

	String message() default "{gov.uk.ets.validation.constraints.PasswordStrength.message}";

	/**
	 * @return password strength must be higher or equal to
	 */
	int min() default 2;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
