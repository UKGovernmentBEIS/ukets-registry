package uk.gov.ets.signing.api.web;

import gov.uk.ets.commons.logging.SecurityLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.ets.signing.api.web.error.ErrorBody;
import uk.gov.ets.signing.api.web.error.UkEtsSigningException;

@ControllerAdvice(assignableTypes = SigningServiceController.class)
@Log4j2
public class SigningServiceControllerAdvice {

    /**
     * Handle signing service errors
     */
    @ExceptionHandler(UkEtsSigningException.class)
    public ResponseEntity<ErrorBody> handleException(
        final UkEtsSigningException exception) {
        ErrorBody errorBody = ErrorBody.from(exception.getMessage());
        ResponseEntity<ErrorBody> errorResponse = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorBody);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }
}
