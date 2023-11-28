package gov.uk.ets.registry.api.account;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class AccountControllerAdvice {
    /**
     * The exception handler for the @link {@link AccountValidationException}.
     * @param exception the exception
     * @return a response entity containing the error messages
     */
    @ExceptionHandler(AccountValidationException.class)
    public ResponseEntity<ErrorBody> handleAccountValidationException(
        final AccountValidationException exception) {
        ErrorBody errorBody = ErrorBody.builder()
            .errorDetails(exception.getErrors()
                .stream()
                .map(error -> ErrorDetail.builder()
                    .message(error.getMessage())
                    .build())
                .collect(Collectors.toList()))
            .build();

        ResponseEntity<ErrorBody> errorResponse = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(AccountActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(
        AccountActionException exception) {
        ErrorBody errorBody = ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .identifier(exception.getAccountActionError().getAccountFullIdentifier())
                .message(exception.getAccountActionError().getMessage())
                .code(exception.getAccountActionError().getCode())
                .urid(exception.getAccountActionError().getUrid()).build())).build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(AccountServiceException exception) {

        ErrorBody errorBody = ErrorBody.builder()
            .errorDetails(exception
                .getAccountServiceErrors().stream().map(error -> ErrorDetail.builder()
                    .code(String.valueOf(error.getCode())).message(error.getMessage()).build())
                .collect(Collectors.toList()))
            .build();

        ResponseEntity<ErrorBody> responseEntity = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
}