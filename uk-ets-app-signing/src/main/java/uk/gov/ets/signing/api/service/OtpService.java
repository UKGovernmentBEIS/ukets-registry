package uk.gov.ets.signing.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.ets.signing.api.SigningProperties;
import uk.gov.ets.signing.api.web.error.UkEtsSigningException;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String OTP_KEY = "otp";

    private final RestTemplate restTemplate;

    private final SigningProperties signingProperties;

    /**
     * Communicates with keycloak and verify that the users OTP code is valid.
     *
     * @param otp         the user's otpCode
     * @param bearerToken the users authorization token
     * @throws UkEtsSigningException when the code is not valid
     */
    public void verifyOtp(String otp, String bearerToken) {
        // if keycloak integration is disabled just move on
        if (!signingProperties.getKeycloak().isEnabled()) {
            return;
        }
        if (otp == null || otp.isBlank()) {
            throw new UkEtsSigningException("OTP code is required.");
        }
        String validatorEndpoint = signingProperties.getKeycloak().getValidatorEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(OTP_KEY, otp);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Boolean> exchange =
            restTemplate.exchange(validatorEndpoint, HttpMethod.POST, request, Boolean.class);
        if (Boolean.FALSE.equals(exchange.getBody())) {
            throw new UkEtsSigningException("Invalid code. Please try again.");
        }
    }
}
