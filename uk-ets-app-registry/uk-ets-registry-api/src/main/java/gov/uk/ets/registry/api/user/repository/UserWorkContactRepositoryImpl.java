package gov.uk.ets.registry.api.user.repository;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.keycloak.KeycloakRestEndpointRepository;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

/**
 * {@link UserWorkContactRepository} Implementation
 */
@Repository
public class UserWorkContactRepositoryImpl
    extends KeycloakRestEndpointRepository<UserWorkContact[]>
    implements UserWorkContactRepository {
    private static final String USER_PERSONAL_INFO_PATH = "/contacts";

    private AuthorizationService authorizationService;
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    public UserWorkContactRepositoryImpl(
        @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
        @Value("${keycloak.realm}") String keycloakRealm,
        @Value("${uk.ets.users.rest.enpoint.path}") String keycloakUsersRestEndpoint,
        RestTemplate restTemplate,
        AuthorizationService authorizationService,
        ServiceAccountAuthorizationService serviceAccountAuthorizationService) {
        super(restTemplate,
            keycloakAuthServerUrl + "/realms/" + keycloakRealm + keycloakUsersRestEndpoint + USER_PERSONAL_INFO_PATH);
        this.authorizationService = authorizationService;
        this.serviceAccountAuthorizationService = serviceAccountAuthorizationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserWorkContact> fetch(Set<String> urids, boolean withServiceAccountAccess) {
        String token = null;
        if (withServiceAccountAccess) {
            token = serviceAccountAuthorizationService.obtainAccessToken().getToken();
        } else {
            token = authorizationService.obtainAccessToken().getToken();
        }
        Map<String, Set<String>> paramsMap = new HashMap<>();
        paramsMap.put("urid", urids);
        UserWorkContact[] responseBody = fetchData(paramsMap, token, UserWorkContact[].class);

        return Arrays.asList(responseBody);
    }
}
