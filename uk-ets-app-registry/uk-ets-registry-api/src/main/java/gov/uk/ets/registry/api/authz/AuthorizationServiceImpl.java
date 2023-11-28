package gov.uk.ets.registry.api.authz;

import com.google.common.collect.Lists;
import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakClientRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepresentationRepository;
import gov.uk.ets.registry.api.user.domain.UserAttributes;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.Configuration;

import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse.EvaluationResultRepresentation;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final int FIRST_ELEMENT = 0;
    private static final String CLAIM_URID = "urid";
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.resource}")
    private String keycloakClientId;
    @Value("${keycloak.credentials.secret}")
    private String keycloakCredentialsSecret;
    private final KeycloakUserRepresentationRepository keycloakUserRepresentationRepository;
    private final KeycloakClientRepresentationRepository keycloakClientRepresentationRepository;

    public AuthorizationServiceImpl(KeycloakUserRepresentationRepository keycloakUserRepresentationRepository,
                                    KeycloakClientRepresentationRepository keycloakClientRepresentationRepository) {
        this.keycloakUserRepresentationRepository = keycloakUserRepresentationRepository;
        this.keycloakClientRepresentationRepository = keycloakClientRepresentationRepository;
    }

    /**
     * @param role
     * @return true if the current logged-in user has the provided role false
     * otherwise
     */
    @Override
    public boolean hasClientRole(UserRole role) {
        AccessToken currentUserToken = getToken();
        // Get the client roles for the loggedIn user
        AccessToken.Access access = currentUserToken.getResourceAccess(keycloakClientId);
        if (access == null) {
            return false;
        }
        Set<String> roles = access.getRoles();
        return roles != null && roles.contains(role.getKeycloakLiteral());
    }

    /**
     * @return the state of the currently logged in user
     */
    public String getUserState() {
        AccessToken currentUserToken = getToken();
        // Get the state for the loggedIn user
        if (currentUserToken.getOtherClaims() != null
            && currentUserToken.getOtherClaims().containsKey(UserAttributes.STATE.getAttributeName())) {
            return (String) currentUserToken.getOtherClaims().get(UserAttributes.STATE.getAttributeName());
        }
        return "";
    }

    /**
     * @return the endpoint for evaluation.
     * @since v0.3.0
     */
    private String getEvaluationEndpoint() {
        StringBuilder sb = new StringBuilder(keycloakAuthServerUrl);
        sb.append("/admin/realms/").append(keycloakRealm);
        sb.append("/clients/").append(getClientUUID());
        sb.append("/authz/resource-server/policy/evaluate");
        return sb.toString();
    }

    /**
     * @return the keycloak primary key of uk-ets-registry-api client
     * @since v0.3.0
     */
    public String getClientUUID() {
        return keycloakClientRepresentationRepository
                .fetchClientDataByClientId(getAuthzClient().obtainAccessToken().getToken(), keycloakClientId).get(FIRST_ELEMENT).getId();
    }

    /**
     * @param iamIdentifier the IAM identifier.
     * @return the keycloak uk-ets-registry-api client roles for the specified.
     * client
     * @since v0.3.0
     */
    public List<RoleRepresentation> getClientLevelRoles(String iamIdentifier) {
        return keycloakUserRepresentationRepository
                .fetchEffectiveRoleDataByUserIdAndClient(getAuthzClient().obtainAccessToken().getToken(), iamIdentifier, getClientUUID());
    }

    /**
     * Run the Keycloak policy evaluation engine for the provide request. Note:This
     * is the same action that is executed from the admin console via the evaluate
     * tab in the authorization services.
     *
     * @param policyEvaluationRequest
     * @return the Policy Evaluation result
     * @since v0.3.0
     */
    @Override
    public EvaluationResultRepresentation evaluate(PolicyEvaluationRequest policyEvaluationRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAuthzClient().obtainAccessToken().getToken());
        HttpEntity<PolicyEvaluationRequest> request = new HttpEntity<>(policyEvaluationRequest, headers);

        return new RestTemplate().postForObject(getEvaluationEndpoint(), request, EvaluationResultRepresentation.class);
    }

    /**
     * {@inheritDoc}
     */
    public AccessToken getToken() {
        return getKeycloakSecurityContext().getToken();
    }

    /**
     * {@inheritDoc}
     */
    public String getTokenString() {
        return getKeycloakSecurityContext().getTokenString();
    }

    @Override
    public String getUrid() {
        return String.valueOf(getToken().getOtherClaims().get(CLAIM_URID));
    }

    /**
     * @return the Set of scopes contained in an RPT
     */
    public Set<String> getScopes() {

        Set<String> scopes = new HashSet<>();
        Collection<Permission> permissions = getToken().getAuthorization().getPermissions();
        for (Permission p : permissions) {
            scopes.addAll(p.getScopes());
        }
        return scopes;
    }

    public boolean hasScopePermission(Scope scope) {
        return getAuthorizationContext().hasScopePermission(scope.getScopeName());
    }

    private AuthzClient getAuthzClient() {
        Configuration configuration = getAuthorizationContext().getClient().getConfiguration();
        final Header header = new BasicHeader(org.apache.http.HttpHeaders.USER_AGENT, "registry-web-initial-security-config");
        final Header header2 = new BasicHeader("X-Request-ID",
                MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));
        final List<Header> headers = Lists.newArrayList(header, header2);
        final HttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();

        Configuration config =
                new Configuration(
                        configuration.getAuthServerUrl(),
                        configuration.getRealm(),
                        configuration.getResource(),
                        //It looks like in KC21 you cannot get the secret from the config
                        //This is just an ugly workaround until we move to KC22
                        //and replace the KC adapter with the one from spring.
                        Map.of("secret",keycloakCredentialsSecret),
                        client);
        return AuthzClient.create(config);
    }

    public ClientAuthorizationContext getAuthorizationContext() {
        return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext().getAuthorizationContext());
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        @SuppressWarnings("unchecked")
        KeycloakPrincipal<KeycloakSecurityContext> principal =
            (KeycloakPrincipal<KeycloakSecurityContext>) authentication
                .getPrincipal();
        return principal.getKeycloakSecurityContext();
    }

    @Override
    public AccessTokenResponse obtainAccessToken() {
        return getAuthzClient().obtainAccessToken();
    }
}
