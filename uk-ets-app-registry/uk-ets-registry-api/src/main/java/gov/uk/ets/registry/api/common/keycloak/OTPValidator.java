package gov.uk.ets.registry.api.common.keycloak;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OTPValidator {
    private static String REALMS = "/realms/";

    private final RestTemplate restTemplate;
    private final String endpoint;
    private final AuthorizationService authorizationService;

    public OTPValidator(RestTemplate restTemplate,
                        @Value("${keycloak.auth-server-url}") String ukEtsRegistryUrl,
                        @Value("${keycloak.realm}") String realm,
                        @Value("${keycloak.otp.validator.path:/otp-validator}") String otpValidatorPath,
                        AuthorizationService authorizationService
    ) {
        endpoint = ukEtsRegistryUrl + REALMS + realm + otpValidatorPath;
        this.restTemplate = restTemplate;
        this.authorizationService = authorizationService;
    }

    /**
     * Validates an OTP for an authenticated user (i.e. one with a bearer token).
     *
     * @param otpCode
     * @return
     */
    public Boolean validate(String otpCode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);
        String token = authorizationService.getTokenString();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("otp", otpCode);
        HttpEntity request = new HttpEntity<>(body, headers);
        HttpEntity<Boolean> response =
            restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, Boolean.class);
        return response.getBody();
    }

    /**
     * Validates an OTP for an un-authenticated user (i.e. one without a bearer token).W
     * We rely on email for user identification.
     *
     * @param otpCode
     * @return
     */
    public Boolean validate(String otpCode, String email) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint + "/nobearer");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("otp", otpCode);
        body.set("email", email);
        HttpEntity request = new HttpEntity<>(body, headers);
        HttpEntity<Boolean> response =
            restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, Boolean.class);
        return response.getBody();
    }
}
