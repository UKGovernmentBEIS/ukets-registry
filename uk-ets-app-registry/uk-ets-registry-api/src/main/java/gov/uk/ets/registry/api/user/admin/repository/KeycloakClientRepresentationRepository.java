package gov.uk.ets.registry.api.user.admin.repository;

import gov.uk.ets.registry.api.common.keycloak.KeycloakRestEndpointRepository;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class KeycloakClientRepresentationRepository extends KeycloakRestEndpointRepository<ClientRepresentation> {
    public KeycloakClientRepresentationRepository(
            @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
            @Value("${keycloak.realm}") String keycloakRealm,
            @Value("${uk.ets.keycloak.clients.rest.endpoint.path}") String keycloakUserSearchEndpointPath,
            RestTemplate restTemplate) {
        super(restTemplate, keycloakAuthServerUrl + "/admin/realms/" + keycloakRealm + keycloakUserSearchEndpointPath);
    }

    public List<ClientRepresentation> fetchClientDataByClientId(String token, String clientId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("clientId", clientId);

        ParameterizedTypeReference<List<ClientRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        return fetchDataList(paramsMap, token, parameterizedTypeReference);
    }

    public RoleRepresentation fetchRoleDataByClientIdAndRoleName(String token, String clientId, String roleName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint + "/" + clientId + "/roles/" + roleName);

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, RoleRepresentation.class).getBody();
    }

    public Set<UserRepresentation> fetchUserDataByClientAndRole(String token, String clientId, String roleName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint + "/" + clientId + "/roles/" + roleName + "/users");

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        ParameterizedTypeReference<Set<UserRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, parameterizedTypeReference).getBody();
    }
}
