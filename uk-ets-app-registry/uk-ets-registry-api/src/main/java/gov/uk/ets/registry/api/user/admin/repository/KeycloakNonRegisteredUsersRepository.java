package gov.uk.ets.registry.api.user.admin.repository;

import gov.uk.ets.registry.api.common.keycloak.KeycloakRestEndpointRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class KeycloakNonRegisteredUsersRepository extends KeycloakRestEndpointRepository<String>  {

    public KeycloakNonRegisteredUsersRepository(
        @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
        @Value("${keycloak.realm}") String keycloakRealm,
        @Value("${uk.ets.users.rest.enpoint.path}") String keycloakUserSearchEndpointPath,
        RestTemplate restTemplate) {
        super(restTemplate, keycloakAuthServerUrl + "/realms/" + keycloakRealm + keycloakUserSearchEndpointPath +  "/expired");
    }
    
    /**
     * Fetches users from keycloak according to the passed criteria.
     * @param beforeDateTime The time in hours the user was created before the scheduler run.
     * @param token The bearer token
     * @return The {@link List&gtString&lt} user ids 
     */
    public List<String> fetchNonRegisteredUsersCreatedBefore(Long beforeDateTime, String token) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("beforeDateTime", beforeDateTime);
        ParameterizedTypeReference<List<String>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        
        return fetchDataList(paramsMap, token, parameterizedTypeReference);
    }
}
