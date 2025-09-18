package gov.uk.ets.ui.logs.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.ui.logs.error.ErrorBody;
import gov.uk.ets.ui.logs.error.ErrorDetail;
import gov.uk.ets.ui.logs.error.UkEtsUiLogsClientException;
import gov.uk.ets.ui.logs.error.UkEtsUiLogsException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * <p> Extending {@link ResponseEntityExceptionHandler} provides out of the box handling of the response body for
 * specific exceptions (specifically setting the body to null). We can extend some methods for specific exceptions
 * to provide a meaningful error message in the body if this is needed (as shown bellow).</p>
 */
@ControllerAdvice
@Log4j2
public class UiLogsResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ErrorBody error = ErrorBody.builder()
            .errorDetails(
                exception.getBindingResult().getFieldErrors()
                    .stream()
                    .map(fieldError ->
                        ErrorDetail.builder().message(fieldError.getField() + " " + fieldError.getDefaultMessage())
                            .build()
                    ).collect(Collectors.toList())
            )
            .build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return super.handleExceptionInternal(exception, error, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = "Unknown error";
        if (exception.getCause() instanceof JsonProcessingException) {
            message = ((JsonProcessingException) exception.getCause()).getOriginalMessage();
        }
        ErrorBody error = ErrorBody.builder()
            .errorDetails(
                List.of(ErrorDetail.builder()
                    .message(message)
                    .build())
            )
            .build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return super.handleExceptionInternal(exception, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles errors for which the client should show a message.
     */
    @ExceptionHandler(UkEtsUiLogsClientException.class)
    public ResponseEntity<ErrorBody> handleUkEtsUiLogsClientException(UkEtsUiLogsClientException exception) {
        ErrorBody error = ErrorBody.builder()
            .errorDetails(
                List.of(ErrorDetail.builder()
                    .message(exception.getMessage())
                    .build())
            )
            .build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(UkEtsUiLogsException.class)
    public ResponseEntity<ErrorBody> handleUkEtsReportsClientException(UkEtsUiLogsException exception) {
        ErrorBody error = ErrorBody.builder()
            .errorDetails(
                List.of(ErrorDetail.builder()
                    .message(exception.getMessage())
                    .build())
            )
            .build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }
}
