package gov.uk.ets.publication.api.file.upload.error;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.file.upload.error.ClamavException;
import gov.uk.ets.publication.api.error.ErrorBody;
import gov.uk.ets.publication.api.error.ErrorDetail;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Log4j2
public class FileUploadControllerAdvice {

    private static final String MAX_SIZE = "The file must be smaller than 2MB";

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
