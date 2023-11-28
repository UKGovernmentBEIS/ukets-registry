package gov.uk.ets.reports.api.authz;

import gov.uk.ets.reports.api.common.keycloak.KeycloakRepository;
import gov.uk.ets.reports.api.error.UkEtsReportsClientException;
import gov.uk.ets.reports.api.roleaccess.service.ReportTypesPerRoleService;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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


@Service
@RequiredArgsConstructor
public class AuthorizationService {

    @Value("${keycloak.resource}")
    private String keycloakClientId;
    private final ReportTypesPerRoleService reportTypesPerRoleService;
    private final KeycloakRepository keycloakRepository;

    public AccessToken getToken() {
        return getKeycloakSecurityContext().getToken();

    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        @SuppressWarnings("unchecked")
        KeycloakPrincipal<KeycloakSecurityContext> principal =
            (KeycloakPrincipal<KeycloakSecurityContext>) authentication
                .getPrincipal();
        return principal.getKeycloakSecurityContext();
    }

    public String getCurrentUserUrid() {
        return (String) getKeycloakSecurityContext().getToken().getOtherClaims().getOrDefault("urid", "");
    }
 
    /**
     * @param role The requesting user role
     * @return true if the current logged-in user has the provided role false otherwise
     */
    public boolean userTokenContainsRole(String role) {
        AccessToken.Access access = getToken().getResourceAccess("uk-ets-registry-api");
        if (access == null) {
            return false;
        }
        Set<String> roles = access.getRoles();
        return roles != null && roles.toString().contains(role);
    }
    
    /**
     * Determines whether the user has access using two criteria
     * the requesting role must be included in the user token
     * the report type, if present, must be included in the eligible types for that role
     */
    public boolean checkUserAccessToReports(ReportRequestingRole role, ReportType type) {
        if (role != null && !userTokenContainsRole(role.toString()) || (type != null &&
                !reportTypesPerRoleService.availableTypesForRole(type, role))) {
            throw new UkEtsReportsClientException("Not authorized");
        } else {
            return true;
        }
    }

    /**
     * User should be admin and request should include token created for service account.
     */
    public boolean userCanRequestRoleChange(String token, ReportRequestingRole role) {
        if (!ReportRequestingRole.administrator.equals(role) || !tokenContains(token, "service-account")) {
            throw new UkEtsReportsClientException("Not authorized");
        } else {
            return true;
        }
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

    private List<ClientRepresentation> getClientsByClientId(String token) {
        return keycloakRepository.fetchClientDataByClientId(token, keycloakClientId);
    }

    /**
     * Removes reports-user role from user with userId.
     */
    public void removeUserRole(String token, String userId) {
        ClientRepresentation client = getClientsByClientId(token).get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakRepository
                .fetchRoleDataByClientIdAndRoleName(token, clientID, "reports-user");
        keycloakRepository.deleteRoleDataByUserIdAndClient(
                token, userId, clientID, Collections.singletonList(clientRole));
    }

    /**
     * Adds reports-user role to user with userId.
     */
    public void addUserRole(String token, String userId) {
        ClientRepresentation client = getClientsByClientId(token).get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakRepository
                .fetchRoleDataByClientIdAndRoleName(token, clientID, "reports-user");
        keycloakRepository.addRoleDataByUserIdAndClient(
                token, userId, clientID, Collections.singletonList(clientRole));
    }

}
