package gov.uk.ets.keycloak.otp.service.factory;

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

import gov.uk.ets.keycloak.otp.service.provider.OTPValidatorResourceProvider;

public class OTPValidatorResourceProviderFactory implements RealmResourceProviderFactory,ServerInfoAwareProviderFactory {

    public static final String ID = "otp-validator";
    
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new OTPValidatorResourceProvider(session);
    }

    @Override
    public void init(Scope config) {
        
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        
    }

    @Override
    public void close() {
        
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
        properties.forEach((k,v) -> ret.put(k.toString(), v.toString()));
        return ret;
    }

}
