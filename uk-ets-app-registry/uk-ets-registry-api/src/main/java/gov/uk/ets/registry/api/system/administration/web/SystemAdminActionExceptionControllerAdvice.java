package gov.uk.ets.registry.api.system.administration.web;

import gov.uk.ets.commons.logging.SecurityLog;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.system.administration.service.SystemAdminActionException;

@ControllerAdvice
@Log4j2
public class SystemAdminActionExceptionControllerAdvice {

    @ExceptionHandler(SystemAdminActionException.class)
    public ResponseEntity<ErrorBody> applicationExceptionHandler(SystemAdminActionException exception) {

        ErrorBody errorBody = ErrorBody.builder()
                .errorDetails(exception
                        .getSystemAdministrationActionErrors().stream().map(error -> ErrorDetail.builder()
                                .code(String.valueOf(error.getCode())).message(error.getMessage()).build())
                        .collect(Collectors.toList()))
                .build();

        ResponseEntity<ErrorBody> responseEntity = new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
}
