package gov.uk.ets.registry.api.authz;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.OAuth2ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.google.common.collect.Lists;

import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakClientRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepresentationRepository;
import lombok.RequiredArgsConstructor;

/**
 * Used to access the token of the system account.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
public class ServiceAccountAuthorizationService implements AccessTokenRetriever {


    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String resource; 
    @Value("${keycloak.realm}")
    private String realm; 
    
    private final KeycloakUserRepresentationRepository keycloakUserRepresentationRepository;
    private final KeycloakClientRepresentationRepository keycloakClientRepresentationRepository;

    private static final String PASSWD_POLICY_VIOLATION = "password_policy_violation";
    private static final String PASSWD_POLICY_VIOLATION_MESSAGE =
        "Password does not conform to password policies.";

    /**
     * Access the token of the system account.
     *
     * @return the access token
     */
    @Override
    public AccessTokenResponse obtainAccessToken() {
        return AuthzClient.create(getConfiguration()).obtainAccessToken();
    }

    /**
     * Returns the Keycloak configuration.
     *
     * @return the Keycloak configuration.
     */
    @NotNull
    private Configuration getConfiguration() {
        final Header header = new BasicHeader("X-Request-ID",
                MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));
        final List<Header> headers = Lists.newArrayList(header);
        return new Configuration(
            authServerUrl,
            realm,
            resource,
            Map.of("secret",secret),
            HttpClientBuilder.create().setDefaultHeaders(headers).build());
    }

    /**
     * Returns a Keycloak realm resource.
     *
     * @return a Keycloak realm resource.
     */
    private RealmResource getClient() {
        Configuration configuration = getConfiguration();
        return Keycloak.getInstance(configuration.getAuthServerUrl(), configuration.getRealm(),
            configuration.getResource(), obtainAccessToken().getToken()).realm(configuration.getRealm());
    }

    /**
     * Retrieves the provided Keycloak user.
     *
     * @param keycloakUserId The user id in Keycloak.
     * @return a Keycloak user.
     */
    public UserRepresentation getUser(String keycloakUserId) {
        return getUser(keycloakUserId, getClient());
    }

    /**
     * Retrieves the provided Keycloak user using the provided realm resource.
     *
     * @param keycloakUserId The user id in Keycloak.
     * @param client         The realm resource.
     * @return a Keycloak user.
     */
    public Response deleteUser(String keycloakUserId) {
        return getClient().users().delete(keycloakUserId);
    }
    
    /**
     * Adds the provided required action to a user.
     *
     * @param keycloakUserId The user id in Keycloak.
     * @param action         The required action.
     */
    public void addRequiredActionToUser(String keycloakUserId, String action) {
        RealmResource client = getClient();

        UserRepresentation userRepresentation = getUser(keycloakUserId, getClient());

        List<String> requiredActions = userRepresentation.getRequiredActions();
        if (requiredActions == null) {
            requiredActions = new ArrayList<>();
        }
        requiredActions.add(action);
        userRepresentation.setRequiredActions(requiredActions);

        final UserResource user = client.users().get(userRepresentation.getId());
        for (CredentialRepresentation credential : user.credentials()) {
            if (CREDENTIAL_OTP.equals(credential.getType())) {
                user.removeCredential(credential.getId());
            }
        }

        user.update(userRepresentation);
    }

    public LocalDateTime getTokenLastUpdateDate(String keycloakUserId) {
        LocalDateTime result = null;
        RealmResource client = getClient();
        UserResource user = client.users().get(keycloakUserId);
        for (CredentialRepresentation credential : user.credentials()) {
            if (CREDENTIAL_OTP.equals(credential.getType())) {
                result = Instant.ofEpochMilli(credential.getCreatedDate()).atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            }
        }
        return result;
    }


    public void resetUserPassword(String email, String newPasswd) {
        RealmResource client = getClient();
        UserRepresentation userRepresentation = client.users().search(email, true).get(0);
        final UserResource user = client.users().get(userRepresentation.getId());
        for (CredentialRepresentation credential : user.credentials()) {
            if ("password".equals(credential.getType())) {
                credential.setValue(newPasswd);
                try {
                    keycloakUserRepresentationRepository.resetPasswordByUserId(obtainAccessToken().getToken(), userRepresentation.getId(), credential);
                } catch (BadRequestException e) {
                    OAuth2ErrorRepresentation error = e.getResponse().readEntity(OAuth2ErrorRepresentation.class);
                    throw new IllegalArgumentException(error.getErrorDescription());
                } catch (HttpClientErrorException.BadRequest e)  {
                    // Since the way to reset the password changed from a JBoss jax-rs to a Spring RestTemplate request,
                    // the error response has changed from a BadRequestException to an HttpClientErrorException.BadRequest.
                    if (!StringUtils.isEmpty(e.getMessage()) && e.getMessage().contains(PASSWD_POLICY_VIOLATION)) {
                        throw new IllegalArgumentException(PASSWD_POLICY_VIOLATION_MESSAGE);
                    }
                }
            }
        }
    }

    /**
     * Delete all active user sessions
     *
     * @param userId The iam identifier of the user
     */
    public void invalidateUserSessions(String userId) {
        RealmResource client = getClient();
        if (!CollectionUtils.isEmpty(client.users().get(userId).getUserSessions())) {
            client.users().get(userId).getUserSessions()
                    .forEach(userSession -> client.deleteSession(userSession.getId(), false));
        }
    }

    /**
     * Triggers a Keycloak flow which sends an email to the user with a link to complete the specified actions.
     */
    public void triggerKeycloakExecuteActionsEmail(String iamIdentifier, String... actions) {
        RealmResource client = getClient();
        client.users().get(iamIdentifier).executeActionsEmail(Arrays.asList(actions));
    }

    /**
     * Invalidate all user sessions.
     */
    public void logoutUser(String iamIdentifier) {
        getClient().users().get(iamIdentifier).logout();
    }

    /**
     * Retrieves the provided Keycloak user using the provided realm resource.
     *
     * @param keycloakUserId The user id in Keycloak.
     * @param client         The realm resource.
     * @return a Keycloak user.
     */
    private UserRepresentation getUser(String keycloakUserId, RealmResource client) {
        return client.users().get(keycloakUserId).toRepresentation();
    }

    private static final String CREDENTIAL_OTP = "otp";

    private ClientRepresentation getRealmClient() {
        return getClient().clients().findByClientId(resource).get(0);
    }
    
    public void removeUserRole(String userId, String role) {
        ClientRepresentation client = getRealmClient();
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakClientRepresentationRepository
                .fetchRoleDataByClientIdAndRoleName(obtainAccessToken().getToken(), clientID, role);
        keycloakUserRepresentationRepository.deleteRoleDataByUserIdAndClient(obtainAccessToken().getToken(), userId, clientID, Collections.singletonList(clientRole));
        
    }
    
	public void updateUserDetails(UserRepresentation user) {
		keycloakUserRepresentationRepository.updateUser(obtainAccessToken().getToken(), user);
	}
    
    /**
     * Retrieves the Keycloak users filtered by the provided single attributes.
     *
     * @param Map<String, String> attributes
     * @return List<UserRepresentation>
     */
    public List<UserRepresentation> searchBySingleAttributes(Map<String, String> attributes) {
    	return getClient().users().searchByAttributes(mapToSearchQuery(attributes));
    }
    
    private String mapToSearchQuery(Map<String, String> search) {
        return search.entrySet()
                .stream()
                .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining(" "));
    }
}
