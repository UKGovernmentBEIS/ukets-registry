package gov.uk.ets.registry.api.file.upload.adhoc;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsBusinessRulesException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@ControllerAdvice
@Log4j2
public class AdHocEmailRecipientsControllerAdvice {

    @ExceptionHandler(AdHocEmailRecipientsBusinessRulesException.class)
    public ResponseEntity<ErrorBody> handleEdHocEmailRecipientsException(
        final AdHocEmailRecipientsBusinessRulesException exception) {
        ErrorBody errorBody = ErrorBody.builder()
            .errorDetails(exception.getAdHocEmailRecipientsErrors()
                .entrySet().stream()
                .map(entry -> {
                    String rowNumbers = entry.getValue().stream().map(String::valueOf).collect(joining(","));
                    String message = entry.getKey().getMessage() + " in rows: " + rowNumbers;
                    return ErrorDetail.builder()
                        .message(message)
                        .build();
                })
                .collect(Collectors.toList()))
            .build();
        ResponseEntity<ErrorBody> responseEntity = ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorBody);
        SecurityLog.log(log, responseEntity.toString());
        return responseEntity;
    }
}
