package gov.uk.ets.publication.api.authz;

import gov.uk.ets.publication.api.common.keycloak.KeycloakRepository;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiClientException;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthorizationService {

    @Value("${keycloak.resource}")
    private String keycloakClientId;
    private final KeycloakRepository keycloakRepository;

    public AccessToken getToken() {
        return getKeycloakSecurityContext().getToken();

    }

    /**
     * User should be admin and request should include token created for service account.
     */
    public boolean userCanRequestRoleChange(String token) {
        if (!tokenContains(token, "service-account")) {
            throw new UkEtsPublicationApiClientException("Not authorized");
        } else {
            return true;
        }
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        @SuppressWarnings("unchecked")
        KeycloakPrincipal<KeycloakSecurityContext> principal =
            (KeycloakPrincipal<KeycloakSecurityContext>) authentication
                .getPrincipal();
        return principal.getKeycloakSecurityContext();
    }

    private boolean tokenContains(String tokenString, String arg) {
        try {
            AccessToken token = TokenVerifier.create(
                    tokenString.substring(tokenString.lastIndexOf(" ") + 1), AccessToken.class).getToken();
            return token.getPreferredUsername().contains(arg);
        } catch (VerificationException e) {
            return false;
        }
    }

    public void addUserRole(String token, String userId) {
        ClientRepresentation client = getClientsByClientId(token).get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakRepository
                .fetchRoleDataByClientIdAndRoleName(token, clientID, "site-publisher");
        keycloakRepository.addRoleDataByUserIdAndClient(
                token, userId, clientID, Collections.singletonList(clientRole));
    }

    /**
     * Removes reports-user role from user with userId.
     */
    public void removeUserRole(String token, String userId) {
        ClientRepresentation client = getClientsByClientId(token).get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakRepository
                .fetchRoleDataByClientIdAndRoleName(token, clientID, "site-publisher");
        keycloakRepository.deleteRoleDataByUserIdAndClient(
                token, userId, clientID, Collections.singletonList(clientRole));
    }

    private List<ClientRepresentation> getClientsByClientId(String token) {
        return keycloakRepository.fetchClientDataByClientId(token, keycloakClientId);
    }
}
