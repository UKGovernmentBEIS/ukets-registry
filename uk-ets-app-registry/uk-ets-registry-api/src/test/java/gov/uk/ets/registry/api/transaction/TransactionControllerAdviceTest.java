package gov.uk.ets.registry.api.transaction;

import static org.junit.Assert.assertEquals;


import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class TransactionControllerAdviceTest {

    @Test
    public void testThatBusinessCheckExceptionThrowsProperErrorObject() {
        BusinessCheckError error = getBusinessCheckError();
        BusinessCheckException exception = new BusinessCheckException(error);
        TransactionControllerAdvice advice = new TransactionControllerAdvice();
        ResponseEntity<ErrorBody> responseEntity = advice.handleBusinessCheckResultException(exception);
        assertEquals(getErrorBody(), responseEntity.getBody());
    }

    @Test
    public void testThatRequiredFieldExceptionsAreHandledProperly() {
        RequiredFieldException exception = new RequiredFieldException("testField");
        TransactionControllerAdvice advice = new TransactionControllerAdvice();
        ResponseEntity<ErrorBody> responseEntity = advice.handleRequiredFieldException(exception);
        assertEquals(getRequiredFieldErrorBody(), responseEntity.getBody());
    }

    private BusinessCheckError getBusinessCheckError() {
        return new BusinessCheckError(
            4040, "A test error"
        );
    }

    private ErrorBody getErrorBody() {
        return ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .code("4040")
                .message("A test error")
                .build()
        )).build();
    }

    private ErrorBody getRequiredFieldErrorBody() {
        return ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .message("testField")
                .build()
            )).build();
    }
}
