package gov.uk.ets.keycloak.users.service.infrastructure;

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

/**
 * Factory for creating {@link UserSearchResourceProvider} instances
 */
public class UserSearchResourceProviderFactory implements RealmResourceProviderFactory,ServerInfoAwareProviderFactory {

    public static final String ID = "uk-ets-users";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new UserSearchResourceProvider(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Scope config) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // NOOP
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
        properties.forEach((k,v) -> ret.put(k.toString(), v.toString()));
        return ret;
    }
}
