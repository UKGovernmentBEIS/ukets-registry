package gov.uk.ets.keycloak.migration.rest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class MigrationManagerResourceProviderFactory
    implements RealmResourceProviderFactory, ServerInfoAwareProviderFactory {

    public static final String ID = "migration-manager";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new MigrationManagerResourceProvider(session);
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

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Map<String, String> ret = new LinkedHashMap<>();
        properties.forEach((k, v) -> ret.put(k.toString(), v.toString()));
        return ret;
    }

}
