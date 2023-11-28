package gov.uk.ets.keycloak.migration.jpa;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class MigrationJpaEntityProviderFactory implements JpaEntityProviderFactory {

    public static final String ID = "migration-entity-provider";

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new MigrationJpaEntityProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Scope config) {
        // nothing here
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // nothing here
    }

    @Override
    public void close() {
        // nothing here
    }

}
