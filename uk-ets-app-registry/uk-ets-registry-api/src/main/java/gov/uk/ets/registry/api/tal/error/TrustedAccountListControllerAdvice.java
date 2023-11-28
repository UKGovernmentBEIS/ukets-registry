package gov.uk.ets.registry.api.tal.error;

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
public class TrustedAccountListControllerAdvice {
    /**
     * An exception handler for the trusted account list actions.
     */
    @ExceptionHandler(TrustedAccountListActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(
        TrustedAccountListActionException exception) {
        ErrorBody errorBody = ErrorBody.builder().errorDetails(
            Collections.singletonList(ErrorDetail.builder()
                .identifier(exception.getTrustedAccountListActionError().getAccountFullIdentifier())
                .message(exception.getTrustedAccountListActionError().getMessage())
                .code(exception.getTrustedAccountListActionError().getCode())
                .urid(exception.getTrustedAccountListActionError().getUrid()).build())).build();
        ResponseEntity<ErrorBody> errorBodyResponseEntity = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorBodyResponseEntity.toString());
        return errorBodyResponseEntity;
    }
}
