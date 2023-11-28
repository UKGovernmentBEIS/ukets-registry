package gov.uk.ets.registry.api.account.shared;

import static org.junit.Assert.assertEquals;


import gov.uk.ets.registry.api.account.AccountControllerAdvice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class AccountControllerAdviceTest {

    @Test
    void testThatExceptionThrowsProperErrorObject() {
        AccountActionException accountActionException = new AccountActionException();
        AccountActionError accountActionError = getAccountActionError();
        accountActionException.setError(accountActionError);

        AccountControllerAdvice advice = new AccountControllerAdvice();
        ResponseEntity<ErrorBody> responseEntity = advice.applicationExceptionHandler(accountActionException);
        assertEquals(getErrorBody(), responseEntity.getBody());
    }

    private AccountActionError getAccountActionError() {
        return new AccountActionError(
            "testCode",
            "testAccountIdentifier",
            "testUrid",
            "testMessage"
        );
    }

    private ErrorBody getErrorBody() {
        return ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .code("testCode")
                .message("testMessage")
                .urid("testUrid")
                .identifier("testAccountIdentifier").build())
        ).build();
    }
}
