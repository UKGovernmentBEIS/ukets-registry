package gov.uk.ets.registry.api.task;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.task.service.TaskActionException;
import gov.uk.ets.registry.api.task.service.TaskServiceException;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class TaskControllerAdvice {

    @ExceptionHandler(TaskActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(
        TaskActionException exception) {
        ErrorBody errorBody = ErrorBody.builder().errorDetails(
            exception.getTaskActionErrors().stream().map(
                error -> ErrorDetail.builder()
                    .code(String.valueOf(error.getCode()))
                    .message(error.getMessage())
                    .urid(error.getUrid())
                    .identifier(String.valueOf(error.getRequestId()))
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


    @ExceptionHandler(TaskServiceException.class)
    public ResponseEntity<ErrorBody> handleTaskServiceException(
        final TaskServiceException message) {
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
}
