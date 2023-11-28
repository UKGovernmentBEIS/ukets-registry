package gov.uk.ets.keycloak.events.service.factory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class KafkaEventListenerProviderFactory implements EventListenerProviderFactory, ServerInfoAwareProviderFactory {
    private static final String ID = "event-producer";
    private KafkaEventListenerProvider instance;

    public EventListenerProvider create(KeycloakSession keycloakSession) {
        // according to the docs "only one instance of a factory exists per server" but
        // if we don't check for the instance here, we get an error that the Kafka producer
        // already exists, which means that the create method is called more than once...
        if (instance == null) {
            instance = new KafkaEventListenerProvider(keycloakSession);
        }
        return instance;
    }

    public void init(Config.Scope scope) {
        // nothing here
    }

    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // nothing here
    }

    public void close() {
        // nothing here
    }

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
