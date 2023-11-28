package gov.uk.ets.registry.api.user;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class UserActionExceptionControllerAdvice {

    /**
     * Handles UserActionErrors.
     */
    @ExceptionHandler(UserActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(UserActionException exception) {
        HttpStatus status =
            UserActionError.ENROLMENT_KEY_EXPIRED.equals(exception.getUserActionError()) ? HttpStatus.GONE :
                HttpStatus.BAD_REQUEST;

        ResponseEntity<ErrorBody> errorBodyResponseEntity = new ResponseEntity<>(ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .message(retrieveMessage(exception))
                .build())
        ).build(), status);

        SecurityLog.log(log, errorBodyResponseEntity.toString());
        return errorBodyResponseEntity;
    }

    /**
     * Checks for a message in the error class and if not found it reverts to the old functionality.
     */
    private String retrieveMessage(UserActionException exception) {
        if (exception.getUserActionError().getMessage().equals("")) {
            return exception.getUserActionError().toString();
        }
        return exception.getUserActionError().getMessage();
    }

}
