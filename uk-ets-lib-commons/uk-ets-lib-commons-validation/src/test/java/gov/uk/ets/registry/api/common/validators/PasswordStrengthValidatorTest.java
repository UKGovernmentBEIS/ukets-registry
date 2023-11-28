package gov.uk.ets.registry.api.common.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PasswordStrengthValidatorTest {

    private static Validator validator;
    
    @BeforeAll
    public static void setUp() throws Exception {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
 
    @Test
    public void shouldMarkPasswordInvalid() throws Exception {
        // given
    	PasswordStrengthValidatorTestDTO request = new PasswordStrengthValidatorTestDTO("passw@rd");
        // when
        Set<ConstraintViolation<PasswordStrengthValidatorTestDTO>> violations = validator.validate(request);
        // then
        assertEquals(1, violations.size());
        
        ConstraintViolation<PasswordStrengthValidatorTestDTO> passwordStrengthViolation = violations.iterator().next();
        
        assertEquals("measured password strength 1 must be greater than 2",passwordStrengthViolation.getMessage());
    }	
    
    @Test
    public void shouldPassValidation() throws Exception {
        // given
    	PasswordStrengthValidatorTestDTO request = new PasswordStrengthValidatorTestDTO("70VsJS4CjfNj3lCakNoJ");
        // when
        Set<ConstraintViolation<PasswordStrengthValidatorTestDTO>> violations = validator.validate(request);
        // then
        assertEquals(0, violations.size());
    }
	
}
