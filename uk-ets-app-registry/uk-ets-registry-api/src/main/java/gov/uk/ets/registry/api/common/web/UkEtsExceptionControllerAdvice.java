package gov.uk.ets.registry.api.common.web;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotAuthorizedException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.ListUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Runtime controller advice.
 */
@ControllerAdvice
@Log4j2
public class UkEtsExceptionControllerAdvice {

    public static final String API_URL_PREFIX = "/api-registry";

    /**
     * Handles bad request exceptions.
     *
     * @param exception the exception to handle
     * @return a {@link ErrorBody}
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorBody> badRequestErrorHandler(Exception exception) {
        ResponseEntity<ErrorBody> errorResponse = getErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    /**
     * Handles not authorized exceptions.
     *
     * @param exception the exception to handle
     * @return a {@link ErrorBody}
     */
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorBody> notAuthorizedErrorHandler(NotAuthorizedException exception) {
        ResponseEntity<ErrorBody> errorResponse = getErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    /**
     * Handles not found exceptions.
     *
     * @param exception the exception to handle
     * @return a {@link ErrorBody}
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorBody> notFoundErrorHandler(NotFoundException exception) {
        ResponseEntity<ErrorBody> errorResponse = getErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    /**
     * Handles rule engine exceptions thrown from {@link gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleAppliance}.
     *
     * @param exception the exception to handle
     * @return a {@link ErrorBody}
     */
    @ExceptionHandler(BusinessRuleErrorException.class)
    public ResponseEntity<ErrorBody> businessRuleErrorException(BusinessRuleErrorException exception) {
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(exception.getErrorBody(), HttpStatus.FORBIDDEN);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    /**
     * Handles jakarta validation errors by handling the {@link MethodArgumentNotValidException} exception.
     * <p>
     * Handles both field-level errors AND class-level errors (From custom constraints/validators).
     *
     * @param exception The {@link MethodArgumentNotValidException} exception
     * @return a {@link ErrorBody}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> methodArgumentNotValidException(MethodArgumentNotValidException exception) {

        List<ErrorDetail> fieldErrors = exception.getBindingResult().getFieldErrors()
            .stream()
            .map(fieldError ->
                ErrorDetail.builder()
                    .message(fieldError.getField() + " " + fieldError.getDefaultMessage())
                    .build()
            ).collect(toList());

        List<ErrorDetail> globalErrors = exception.getBindingResult().getGlobalErrors().stream()
            .map(objectError -> ErrorDetail.builder().message(objectError.getDefaultMessage()).build())
            .collect(toList());

        ErrorBody error = ErrorBody.builder()
            .errorDetails(ListUtils.union(fieldErrors, globalErrors))
            .build();

        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    /**
     * <p>Checks first if the Http Request is an API call (by checking if it contains the
     * {@link UkEtsExceptionControllerAdvice#API_URL_PREFIX}). If this is the case, an error is returned since the API
     * call cannot be handled by the application (Wrong resource URL).</p>
     *
     * <p>If the request is not an API call, it means that it is a client side route and must be handled by Angular,
     * thus it is forwarded to index.html.</p>
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public Object handleNoHandleFoundException(final NoHandlerFoundException exception, HttpServletRequest req,
                                               RedirectAttributes redirectAttributes) {
        String relativeUrl = req.getRequestURI().substring(req.getContextPath().length());
        if (relativeUrl.startsWith(API_URL_PREFIX)) {
            ResponseEntity<ErrorBody> errorResponse = getErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
            SecurityLog.log(log, errorResponse.toString());
            return errorResponse;
        } else {
            return "forward:/index.html";
        }
    }

    /**
     * Handles unsupported media type exceptions.
     *
     * @param exception the exception to handle
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorBody> notAcceptableMediaTypeHandler(final HttpMediaTypeNotSupportedException exception) {
        ErrorBody error = ErrorBody.builder()
            .errorDetails(
                exception.getSupportedMediaTypes()
                    .stream()
                    .map(fieldError ->
                        ErrorDetail.builder().message("The request contains bad syntax or cannot be " +
                                "fulfilled")
                            .build()
                    ).collect(toList())
            )
            .build();
        ResponseEntity<ErrorBody> errorResponse = new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }

    private ResponseEntity<ErrorBody> getErrorResponse(String message, HttpStatus httpStatus) {
        ErrorBody error = ErrorBody.builder()
            .errorDetails(Collections.singletonList(ErrorDetail.builder().message(message).build()))
            .build();
        return new ResponseEntity<>(error, httpStatus);
    }
}
