package gov.uk.ets.registry.api.user.admin.repository;

import gov.uk.ets.registry.api.common.keycloak.KeycloakRestEndpointRepository;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchCriteria;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchPagedResults;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * The keycloak users repository
 */
@Repository
public class KeycloakUserRepository extends KeycloakRestEndpointRepository<KeycloakUserSearchPagedResults> {
    public KeycloakUserRepository(
        @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
        @Value("${keycloak.realm}") String keycloakRealm,
        @Value("${uk.ets.users.rest.enpoint.path}") String keycloakUserSearchEndpointPath,
        RestTemplate restTemplate) {
        super(restTemplate, keycloakAuthServerUrl + "/realms/" + keycloakRealm + keycloakUserSearchEndpointPath);
    }

    /**
     * Fetches users from keycloak according to the passed criteria.
     * @param criteria The users criteria
     * @param token The bearer token
     * @return The {@link KeycloakUserSearchPagedResults} results
     * @throws RestClientException
     */
    public KeycloakUserSearchPagedResults fetch(KeycloakUserSearchCriteria criteria, String token) {
        Map<String, Object> paramsMap = new HashMap<>();
        Stream.of(new Object[][] {
            {"nameOrUserId", criteria.getNameOrUserId()},
            {"email", criteria.getEmail()},
            {"lastSignInFrom", criteria.getLastSignInFrom()},
            {"lastSignInTo", criteria.getLastSignInTo()},
            {"role", criteria.getRole()},
            {"status", criteria.getStatus()},
            {"sortField", criteria.getSortField()},
            {"sortDirection", criteria.getSortDirection()},
            {"page", criteria.getPage()},
            {"pageSize", criteria.getPageSize()}
        }).forEach(param -> {
            paramsMap.put((String)param[0], param[1]);
        });

        return fetchData(paramsMap, token, KeycloakUserSearchPagedResults.class);
    }
}
