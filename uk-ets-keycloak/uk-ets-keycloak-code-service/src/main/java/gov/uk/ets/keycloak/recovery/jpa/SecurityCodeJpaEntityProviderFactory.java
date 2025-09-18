package gov.uk.ets.keycloak.recovery.jpa;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SecurityCodeJpaEntityProviderFactory implements JpaEntityProviderFactory {

    public static final String ID = "security-code-entity-provider";

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new SecurityCodeJpaEntityProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
