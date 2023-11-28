package gov.uk.ets.ui.logs.authz;

import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthorizationService {

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
}
