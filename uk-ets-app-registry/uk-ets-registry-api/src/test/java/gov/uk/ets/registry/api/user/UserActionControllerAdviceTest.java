package gov.uk.ets.registry.api.user;

import static org.junit.Assert.assertEquals;


import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class UserActionControllerAdviceTest {

    @Test
    public void testThatBusinessCheckExceptionThrowsProperErrorObject() {
        UserActionExceptionControllerAdvice advice = new UserActionExceptionControllerAdvice();
        UserActionException exception = new UserActionException();
        exception.setUserActionError(UserActionError.ENROLMENT_KEY_EXPIRED);
        ResponseEntity<ErrorBody> responseEntity = advice.applicationExceptionHandler(exception);
        assertEquals(getErrorBody(), responseEntity.getBody());
    }

    private ErrorBody getErrorBody() {
        return ErrorBody.builder().errorDetails(
            Arrays.asList(ErrorDetail.builder()
                .message(UserActionError.ENROLMENT_KEY_EXPIRED.toString())
                .build()
            )).build();
    }
}
