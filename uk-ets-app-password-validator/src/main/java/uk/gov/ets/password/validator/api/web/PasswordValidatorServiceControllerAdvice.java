package uk.gov.ets.password.validator.api.web;

import gov.uk.ets.commons.logging.SecurityLog;
import java.util.Iterator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.ets.password.validator.api.web.model.ValidatePasswordResponse;

@ControllerAdvice(assignableTypes = PasswordValidatorServiceController.class)
@Log4j2
public class PasswordValidatorServiceControllerAdvice {

    /**
     * Handle signing service errors.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidatePasswordResponse> handleException(final ConstraintViolationException exception) {

    	String errorCode = "";
    	StringBuilder errorMessage = new StringBuilder();
    	Iterator<ConstraintViolation<?>> violationsIterator = exception.getConstraintViolations().iterator();
    	
    	while(violationsIterator.hasNext()) {
    		ConstraintViolation<?> violation = violationsIterator.next();
        	String annotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();

        	if(annotation.endsWith("PasswordStrength")) {
        		errorCode= "strength";
        	}    		
        	
        	errorMessage.append(violation.getMessage());
    	}

		ResponseEntity<ValidatePasswordResponse> responseEntity = ResponseEntity.status(HttpStatus.OK).body(
			new ValidatePasswordResponse(false, errorCode,
				exception.getConstraintViolations().iterator().next().getMessage()));
    	SecurityLog.log(log, responseEntity.toString());
		return responseEntity;
    }
    
}
