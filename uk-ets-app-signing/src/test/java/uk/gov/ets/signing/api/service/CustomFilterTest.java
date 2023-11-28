package uk.gov.ets.signing.api.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ets.signing.api.web.filter.CustomLoggingAndExceptionHandlingFilter;

@ExtendWith(MockitoExtension.class)
class CustomFilterTest {

    @Mock
    ServletRequest request;

    @Mock
    ServletResponse response;

    @Mock
    FilterChain chain;

    @InjectMocks
    CustomLoggingAndExceptionHandlingFilter filter;

    @BeforeEach
    void setup() {
        filter = new CustomLoggingAndExceptionHandlingFilter();
    }

    @Test
    void testEmptyImage() {
        assertEquals("", filter.userId(null));
        assertEquals("", filter.etsUserId(null));
    }

    @Test
    void testExceptionCaught() {
        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
    }
}
