package gov.uk.ets.keycloak.users.service.infrastructure;

import org.keycloak.headers.SecurityHeadersProvider;
import org.keycloak.headers.SecurityHeadersProviderFactory;
import org.keycloak.models.KeycloakSession;

public class CustomSecurityHeadersProviderFactory implements SecurityHeadersProviderFactory {

    private static final String ID = "default";
    @Override
    public SecurityHeadersProvider create(KeycloakSession session) {
        return new CustomSecurityHeadersProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }
}
