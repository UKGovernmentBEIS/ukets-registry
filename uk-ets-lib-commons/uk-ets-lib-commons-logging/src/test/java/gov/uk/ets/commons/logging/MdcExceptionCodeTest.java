
package gov.uk.ets.commons.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.NestedServletException;

class MDCExceptionCodeTest {

    @Test
    void shouldReturnSingleErrorCode() {
        NestedServletException exception = new NestedServletException("", new IllegalArgumentException());
        String codes = MDCExceptionCode.retrieveErrorCodeForException(exception);

        assertThat(codes).isEqualTo("ETS-0002");
    }

    @Test
    void shouldReturnMultipleErrorCodes() {
        IllegalArgumentException exception = new IllegalArgumentException("", new NullPointerException());
        String codes = MDCExceptionCode.retrieveErrorCodeForException(exception);

        assertThat(codes).contains("ETS-0001");
        assertThat(codes).contains(",");
        assertThat(codes).contains("ETS-0002");
    }

    @Test
    void shouldReturnUnknown() {
        NestedServletException exception = new NestedServletException("", new IllegalStateException());
        String codes = MDCExceptionCode.retrieveErrorCodeForException(exception);

        assertThat(codes).isEqualTo("ETS-0000");
    }
    
    @Test
    void shouldReturnUnknownWithNullCause() {
        NestedServletException exception = new NestedServletException("",null);
        String codes = MDCExceptionCode.retrieveErrorCodeForException(exception);

        assertThat(codes).isEqualTo("ETS-0000");
    }
}

