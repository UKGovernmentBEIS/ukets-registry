package uk.gov.ets.registration.user.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * <p> Extending {@link ResponseEntityExceptionHandler} provides out of the box handling of the response body for specific </p>
 * <p> exceptions (specifically setting the body to null). We can extend some methods for specific exceptions to provide </p>
 * <p> a meaningful error message in the body if this is needed (as shown bellow).</p>
 */
@ControllerAdvice
public class RegistrationResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        InvalidFormatException originalException = (InvalidFormatException) exception.getCause();
        String customMessage =
            originalException.getOriginalMessage() + " -- location: " + originalException.getLocation();
        return super.handleExceptionInternal(exception,
            customMessage, headers, status, request);
    }
}
