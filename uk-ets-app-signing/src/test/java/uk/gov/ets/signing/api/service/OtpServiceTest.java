package uk.gov.ets.signing.api.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.ets.signing.api.SigningProperties;
import uk.gov.ets.signing.api.web.error.UkEtsSigningException;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    RestTemplate restTemplate;
    OtpService otpService;
    SigningProperties signingProperties = new SigningProperties();

    @BeforeEach
    void before() {
        signingProperties = new SigningProperties();
        otpService = new OtpService(restTemplate, signingProperties);
    }

    @Test
    void testEmptyOtp() {
        assertThrows(UkEtsSigningException.class, () -> otpService.verifyOtp(null, "totem"));
        assertThrows(UkEtsSigningException.class, () -> otpService.verifyOtp("", "totem"));
    }

    @Test
    void testKeycloakFalse() {
        SigningProperties.Keycloak keycloak = new SigningProperties.Keycloak();
        keycloak.setEnabled(false);
        signingProperties.setKeycloak(keycloak);
        assertDoesNotThrow(() -> otpService.verifyOtp("123456", "totem"));
    }

    @Test
    void test() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        OtpService mockOtpService = mock(OtpService.class);
        ResponseEntity<Boolean> trueResponse = new ResponseEntity<>(true, HttpStatus.OK);
        lenient()
            .doReturn(trueResponse)
            .when(mockRestTemplate)
            .exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class));
        assertDoesNotThrow(() -> mockOtpService.verifyOtp("123456", "totem"));
    }
}
