package gov.uk.ets.keycloak.users.service.adapter.rest;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken.Access;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

/**
 * The permission checker. It is responsible on checking the access of bearer token.
 */
public class PermissionsChecker {
    private final AuthenticationManager.AuthResult auth;

    public PermissionsChecker(KeycloakSession session) {
    	AppAuthManager.BearerTokenAuthenticator  authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
    	auth =  authenticator.authenticate();
    }

    /**
     * Checks access and gives access only to users that have access to realm.
     */
    public void checkPermissions() {
        if (auth == null ||
            auth.getToken().getResourceAccess() == null ||
            auth.getToken().getResourceAccess().get("realm-management") == null) {
            throw new NotAuthorizedException("Bearer");
        }
        Access realmMgmt = auth.getToken().getResourceAccess().get("realm-management");
        if(!realmMgmt.getRoles().contains("query-users")) {
            throw new ForbiddenException();
        }
    }

}
