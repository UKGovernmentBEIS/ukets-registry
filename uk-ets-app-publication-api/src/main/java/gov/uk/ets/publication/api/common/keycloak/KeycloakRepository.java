package gov.uk.ets.publication.api.common.keycloak;

import gov.uk.ets.commons.logging.MDCWrapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KeycloakRepository {

    protected RestTemplate restTemplate;
    private final String roleMappingClientPath;
    private final String authServer;
    private final String clientPath;
    private final String userPath;

    public static final String REALMS_PATH = "/admin/realms/";

    public KeycloakRepository(
            RestTemplate restTemplate,
            @Value("${uk.ets.keycloak.role.mapping.clients.rest.endpoint.path}") String roleMappingClientPath,
            @Value("${keycloak.auth-server-url}") String authServer,
            @Value("${uk.ets.keycloak.users.rest.endpoint.path}") String userPath,
            @Value("${uk.ets.keycloak.clients.rest.endpoint.path}") String clientPath) {
        this.restTemplate = restTemplate;
        this.roleMappingClientPath = roleMappingClientPath;
        this.authServer = authServer;
        this.clientPath = clientPath;
        this.userPath = userPath;
    }

    public List<ClientRepresentation> fetchClientDataByClientId(String token, String clientId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("clientId", clientId);

        ParameterizedTypeReference<List<ClientRepresentation>> parameterizedTypeReference =
                new ParameterizedTypeReference<>() {};
        return fetchDataList(paramsMap, token, parameterizedTypeReference);
    }

    public RoleRepresentation fetchRoleDataByClientIdAndRoleName(
            String token, String clientId, String roleName) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(
                        authServer + REALMS_PATH + clientPath + "/" + clientId + "/roles/" + roleName);

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        return restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, request, RoleRepresentation.class).getBody();
    }

    public RoleRepresentation addRoleDataByUserIdAndClient(String token, String userId, String clientId,
                                                           List<RoleRepresentation> roles) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(authServer + REALMS_PATH + userPath + "/" + userId + roleMappingClientPath + "/" + clientId);

        HttpEntity<?> request = new HttpEntity<>(roles, generateHeaders(token));

        return restTemplate.exchange(
                builder.toUriString(), HttpMethod.POST, request, RoleRepresentation.class).getBody();
    }

    public RoleRepresentation deleteRoleDataByUserIdAndClient(String token, String userId, String clientId,
                                                              List<RoleRepresentation> roles) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(authServer + REALMS_PATH + userPath + "/" + userId + roleMappingClientPath + "/" + clientId);

        HttpEntity<?> request = new HttpEntity<>(roles, generateHeaders(token));

        return restTemplate.exchange(
                builder.toUriString(), HttpMethod.DELETE, request, RoleRepresentation.class).getBody();
    }

    protected List<ClientRepresentation> fetchDataList(Map<String, ?> queryParamsMap, String token,
                                                       ParameterizedTypeReference<List<ClientRepresentation>> requestType) {
        UriComponentsBuilder builder = createURI(queryParamsMap);
        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, requestType).getBody();
    }

    protected UriComponentsBuilder createURI(Map<String, ?> queryParamsMap) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                authServer + REALMS_PATH + clientPath);
        queryParamsMap.forEach((key, value) -> {
            if (value != null) {
                if (value instanceof Collection) {
                    builder.queryParam(key, (Collection) value);
                } else {
                    builder.queryParam(key, value);
                }
            }
        });
        return builder;
    }

    protected HttpHeaders generateHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, token);
        headers.set("X-Request-ID",
                MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));
        return headers;
    }
}
