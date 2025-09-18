package gov.uk.ets.registry.api.ar.exception;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class ArControllerAdvice {

    /**
     * The exception handler for the @link {@link UserNotFoundException}.
     * @param exception the exception
     * @return a response entity containing the error message
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorBody> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorBody errorBody = ErrorBody.builder()
            .errorDetails(Collections.singletonList(
                ErrorDetail.builder()
                    .message(exception.getMessage())
                    .componentId(exception.getComponentId())
                    .build()))
            .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
}
