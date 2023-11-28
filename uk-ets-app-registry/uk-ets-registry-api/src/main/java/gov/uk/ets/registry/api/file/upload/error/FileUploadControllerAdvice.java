package gov.uk.ets.registry.api.file.upload.error;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.file.upload.error.ClamavException;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableYearException;
import gov.uk.ets.registry.api.file.upload.bulkar.error.BulkArBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableBusinessRulesException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileUploadControllerAdvice {

    private static final String MAX_SIZE = "The file must be smaller than 2MB";

    @Value("${registry.file.max.errors.size}")
    private String maxErrorsSize;

    /**
     * The exception handler for the @link {@link FileTypeNotValidException}.
     * @param message the exception message
     * @return a response entity containing the error message
     */
    @ExceptionHandler(FileTypeNotValidException.class)
    public ResponseEntity<ErrorBody> handleFileTypeException(
        final FileTypeNotValidException message) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(Collections.singletonList(
                                           ErrorDetail.builder()
                                                      .message(message.getMessage())
                                                      .build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link FileNameNotValidException}.
     * @param message the exception message
     * @return a response entity containing the error message
     */
    @ExceptionHandler(FileNameNotValidException.class)
    public ResponseEntity<ErrorBody> handleFileNameException(
        final FileNameNotValidException message) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(Collections.singletonList(
                                           ErrorDetail.builder()
                                                      .message(message.getMessage())
                                                      .build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link MaxUploadSizeExceededException}.
     * @return a response entity containing the error message
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorBody> handleFileSizeException() {

        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(Collections.singletonList(
                                           ErrorDetail.builder()
                                                      .message(MAX_SIZE)
                                                      .build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link AllocationTableBusinessRulesException}.
     * @param exceptions the list of the exception messages
     * @return a response entity containing the error message
     */
    @ExceptionHandler(AllocationTableBusinessRulesException.class)
    public ResponseEntity<ErrorBody> handleAllocationTableBusinessRuleException(
        final AllocationTableBusinessRulesException exceptions) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(exceptions.getAllocationTableErrorList()
                                                               .stream()
                                                               .map(error -> ErrorDetail.builder()
                                                                                        .message(error.getMessage())
                                                                                        .build())
                                                               .collect(Collectors.toList()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link AllocationTableYearException}.
     * @param exceptions the list of the exception messages
     * @return a response entity containing the error message
     */
    @ExceptionHandler(AllocationTableYearException.class)
    public ResponseEntity<ErrorBody> handleAllocationTableYearException(
        final AllocationTableYearException exception) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(List.of(ErrorDetail.builder().message(exception.getMessage()).build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }    
    
    /**
     * The exception handler for the @link {@link EmissionsTableBusinessRulesException}.
     * @param exceptions the list of the exception messages
     * @return a response entity containing the error message
     */
    @ExceptionHandler(EmissionsTableBusinessRulesException.class)
    public ResponseEntity<ErrorBody> handleEmissionsTableBusinessRuleException(
        final EmissionsTableBusinessRulesException exception) {
    	
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(exception.getEmissionsTableErrorList()
                                                               .stream()
                                                               .map(error -> ErrorDetail.builder()
                                                                                        .message(error.getErrorMessage())
                                                                                        .build())
                                                               .collect(Collectors.toList()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
    
    @ExceptionHandler(BulkArBusinessRulesException.class)
    public ResponseEntity<ErrorBody> handleBulkArBusinessRuleException(
        final BulkArBusinessRulesException exceptions) {

        int errorsListSize = exceptions.getBulkArErrorList().size();
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(exceptions.getBulkArErrorList()
                                                               .subList(0, Math.min(errorsListSize, Integer.parseInt(maxErrorsSize) + 1))
                                                               .stream()
                                                               .map(error -> ErrorDetail.builder()
                                                                                        .message(error)
                                                                                        .build())
                                                               .collect(Collectors.toList()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link FileUploadException}.
     * @param message the exception message
     * @return a response entity containing the error message
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorBody> handleFileUploadException(
        final FileUploadException message) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(Collections.singletonList(
                                           ErrorDetail.builder()
                                                      .message(message.getMessage())
                                                      .build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }

    /**
     * The exception handler for the @link {@link FileUploadException}.
     * @param message the exception message
     * @return a response entity containing the error message
     */
    @ExceptionHandler(ClamavException.class)
    public ResponseEntity<ErrorBody> handleClamavException(
        final ClamavException message) {
        ErrorBody errorBody = ErrorBody.builder()
                                       .errorDetails(Collections.singletonList(
                                           ErrorDetail.builder()
                                                      .message(message.getMessage())
                                                      .build()))
                                       .build();

        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                                                 .body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
}
