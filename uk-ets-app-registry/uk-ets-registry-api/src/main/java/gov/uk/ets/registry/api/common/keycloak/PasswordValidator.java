package gov.uk.ets.registry.api.common.keycloak;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.user.profile.web.PasswordValidationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PasswordValidator {
    private static String REALMS = "/realms/";

    private final RestTemplate restTemplate;
    private final String endpoint;
    private final AuthorizationService authorizationService;

    public PasswordValidator(RestTemplate restTemplate,
                             @Value("${keycloak.auth-server-url}") String ukEtsRegistryUrl,
                             @Value("${keycloak.realm}") String realm,
                             @Value("${keycloak.password.validator.path:/password-validator}")
                                 String passwordValidatorPath,
                             AuthorizationService authorizationService
    ) {
        endpoint = ukEtsRegistryUrl + REALMS + realm + passwordValidatorPath;
        this.restTemplate = restTemplate;
        this.authorizationService = authorizationService;
    }

    /**
     * Validates a password for an authenticated user (i.e. one with a bearer token).
     */
    public boolean validate(PasswordValidationRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);
        String token = authorizationService.getTokenString();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<PasswordValidationRequest> entity = new HttpEntity<>(request, headers);
        HttpEntity<Boolean> response =
            restTemplate.postForEntity(builder.toUriString(), entity, Boolean.class);
        return response.getBody() != null && response.getBody();
    }
}
