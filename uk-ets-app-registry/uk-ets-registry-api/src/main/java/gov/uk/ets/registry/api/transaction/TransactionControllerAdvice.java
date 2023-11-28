package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class TransactionControllerAdvice {

  @ExceptionHandler(BusinessCheckException.class)
  public ResponseEntity<ErrorBody> handleBusinessCheckResultException(
      final BusinessCheckException businessCheckResult) {
    ErrorBody errorBody = ErrorBody.builder().errorDetails(
        businessCheckResult.getBusinessCheckErrorResult().getErrors().stream().map(
            checkError ->  ErrorDetail.builder()
              .code(String.valueOf(checkError.getCode()))
              .message(checkError.getMessage())
              .build()
        ).collect(Collectors.toList())
    ).build();
    ResponseEntity<ErrorBody> errorResponse = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorBody);
    SecurityLog.log(log, errorResponse.toString());
    return errorResponse;
  }

  @ExceptionHandler(RequiredFieldException.class)
  public ResponseEntity<ErrorBody> handleRequiredFieldException(
      final RequiredFieldException message) {
    ErrorBody errorBody = ErrorBody.builder().errorDetails(
        Arrays.asList(ErrorDetail.builder().message(message.getMessage()).build())).build();
    ResponseEntity<ErrorBody> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    SecurityLog.log(log, errorResponse.toString());
    return errorResponse;
  }
}
