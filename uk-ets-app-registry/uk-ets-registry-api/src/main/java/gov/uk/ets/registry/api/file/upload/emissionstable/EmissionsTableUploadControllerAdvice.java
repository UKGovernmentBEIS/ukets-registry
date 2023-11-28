package gov.uk.ets.registry.api.file.upload.emissionstable;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionException;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class EmissionsTableUploadControllerAdvice {

    @ExceptionHandler(EmissionsUploadActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(EmissionsUploadActionException exception) {
        ErrorBody errorBody = ErrorBody.builder().errorDetails(
            exception.getEmissionsUploadActionErrors().stream().map(
                error -> ErrorDetail.builder()
                    .code(String.valueOf(error.getCode()))
                    .message(error.getMessage())
                    .componentId(error.getComponentId())
                    .errorFileId(error.getErrorFileId())
                    .errorFilename(error.getErrorFilename())
                    .build()
            ).collect(Collectors.toList())
        ).build();
        ResponseEntity<ErrorBody> errorBodyResponseEntity = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, errorBodyResponseEntity.toString());
        return errorBodyResponseEntity;
    }
}
