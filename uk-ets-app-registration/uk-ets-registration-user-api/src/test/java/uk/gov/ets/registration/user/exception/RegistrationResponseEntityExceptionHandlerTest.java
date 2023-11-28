package uk.gov.ets.registration.user.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ExtendWith(MockitoExtension.class)
class RegistrationResponseEntityExceptionHandlerTest {

    @Mock
    HttpMessageNotReadableException exception;

    @Mock
    InvalidFormatException exceptionCause;

    @Test
    void shouldSetCorrectErrorMessageInBody() {
        when(exception.getCause()).thenReturn(exceptionCause);
        when(exceptionCause.getOriginalMessage()).thenReturn("test message");
        JsonLocation jsonLocation = new JsonLocation(null, 0, 0, 0);
        when(exceptionCause.getLocation()).thenReturn(jsonLocation);
        RegistrationResponseEntityExceptionHandler handler = new RegistrationResponseEntityExceptionHandler();
        ResponseEntity<Object> objectResponseEntity = handler.handleHttpMessageNotReadable(
                exception,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
               null
        );
        assertThat(objectResponseEntity.getBody())
        .isEqualTo("test message -- location: [Source: UNKNOWN; byte offset: #UNKNOWN]");
    }

}
