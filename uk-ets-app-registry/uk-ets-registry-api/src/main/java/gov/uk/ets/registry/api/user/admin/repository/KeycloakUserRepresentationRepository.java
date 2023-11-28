package gov.uk.ets.registry.api.user.admin.repository;

import gov.uk.ets.registry.api.common.keycloak.KeycloakRestEndpointRepository;
import org.keycloak.representations.idm.CredentialRepresentation;
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

@Repository
public class KeycloakUserRepresentationRepository extends KeycloakRestEndpointRepository<UserRepresentation> {

    private final String roleMappingClientPath;

    public KeycloakUserRepresentationRepository(
            @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
            @Value("${keycloak.realm}") String keycloakRealm,
            @Value("${uk.ets.keycloak.users.rest.endpoint.path}") String keycloakUsersPath,
            @Value("${uk.ets.keycloak.role.mapping.clients.rest.endpoint.path}")
                    String keycloakRoleMappingClientSearchEndpointPath,
            RestTemplate restTemplate) {
        super(restTemplate, keycloakAuthServerUrl + "/admin/realms/" + keycloakRealm + keycloakUsersPath);
        this.roleMappingClientPath = keycloakRoleMappingClientSearchEndpointPath;
    }

    public UserRepresentation fetchUserDataById(String token, String id) {
        return fetchDataById(id, token, UserRepresentation.class);
    }

    public void updateUser(String token, UserRepresentation user) {
        update(token, UserRepresentation.class, user, user.getId());
    }

    public List<UserRepresentation> fetchUserDataByUserName(String token, String userName) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("username", userName);
        paramsMap.put("exact", true);

        ParameterizedTypeReference<List<UserRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        return fetchDataList(paramsMap, token, parameterizedTypeReference);
    }

    public RoleRepresentation addRoleDataByUserIdAndClient(String token, String userId, String clientId,
                                                           List<RoleRepresentation> roles) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint + "/" + userId + roleMappingClientPath + "/" + clientId);

        HttpEntity<?> request = new HttpEntity<>(roles, generateHeaders(token));

        return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, RoleRepresentation.class).getBody();
    }

    public RoleRepresentation deleteRoleDataByUserIdAndClient(String token, String userId, String clientId,
                                                              List<RoleRepresentation> roles) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint + "/" + userId + roleMappingClientPath + "/" + clientId);

        HttpEntity<?> request = new HttpEntity<>(roles, generateHeaders(token));

        return restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request, RoleRepresentation.class).getBody();
    }

    public List<RoleRepresentation> fetchRoleDataByUserIdAndClient(String token, String userId, String clientId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint + "/" + userId + roleMappingClientPath + "/" + clientId);

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        ParameterizedTypeReference<List<RoleRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, parameterizedTypeReference).getBody();
    }

    public List<RoleRepresentation> fetchEffectiveRoleDataByUserIdAndClient(String token, String userId, String clientId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint + "/" + userId + roleMappingClientPath + "/" + clientId + "/composite");

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        ParameterizedTypeReference<List<RoleRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, parameterizedTypeReference).getBody();
    }

    public void resetPasswordByUserId(String token, String userId, CredentialRepresentation credentialRepresentation) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpoint + "/" + userId + "/reset-password");

        HttpEntity<?> request = new HttpEntity<>(credentialRepresentation, generateHeaders(token));

        restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request, CredentialRepresentation.class).getBody();
    }
}