package gov.uk.ets.registry.api.allocation.error;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadActionException;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class AllocationControllerAdvice {

    /**
     * Handle Allocation BR-specific errors.
     */
    @ExceptionHandler(AllocationBusinessRulesException.class)
    public ResponseEntity<ErrorBody> handleAllocationBusinessRulesException(
        final AllocationBusinessRulesException exceptions) {
        ErrorBody errorBody = ErrorBody.builder()
            .errorDetails(exceptions.getAllocationErrorList()
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

    @ExceptionHandler(AllocationTableUploadActionException.class)
    public ResponseEntity<ErrorBody> handleAllocationUploadActionException(
        final AllocationTableUploadActionException exceptions) {
        ErrorBody errorBody =
            ErrorBody.builder()
                     .errorDetails(exceptions.getAllocationTableActionErrors()
                                             .stream()
                                             .map(error -> ErrorDetail.builder()
                                                                      .code(String.valueOf(error.getCode()))
                                                                      .message(error.getMessage())
                                                                      .componentId(error.getComponentId())
                                                                      .errorFileId(error.getErrorFileId())
                                                                      .errorFilename(error.getErrorFilename())
                                                                      .build())
                                             .collect(Collectors.toList()))
                     .build();

        ResponseEntity<ErrorBody> errorResponse = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, errorResponse.toString());
        return errorResponse;
    }
}
