package uk.gov.ets.registration.user.validation;

import gov.uk.ets.commons.logging.SecurityLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.ets.registration.user.exception.UserSubmissionValidationException;

/**
 * Validation advice on user submission.
 */
@ControllerAdvice
@Log4j2
public class UserRegistrationValidatorAdvice {

    /**
     * Method handling the validation exception.
     * @param exception The validation exception
     * @return a {@link ValidationErrorResponse}
     */
    @ExceptionHandler(UserSubmissionValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onValidationException(UserSubmissionValidationException exception) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setViolations(exception.getErrors());
        SecurityLog.log(log, error.toString());
        return error;
    }

}
