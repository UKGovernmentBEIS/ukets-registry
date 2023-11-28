package gov.uk.ets.keycloak.authentication.authenticators.session.limit;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class UserSessionLimitAuthenticatorFactory implements AuthenticatorFactory  {
	
    public static final String USER_CLIENT_LIMIT = "userClientLimit";
    public static final String PROVIDER_ID =  "user-session-limit";
    public static final Integer ONE_SESSION_ONLY =  Integer.valueOf(1);


    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED
    };

    @Override
    public String getDisplayType() {
        return "User session count limiter";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Configures how many concurrent sessions a single user is allowed to create for this realm and/or client";
    }
    
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty userClientLimit = new ProviderConfigProperty();
        userClientLimit.setName(USER_CLIENT_LIMIT);
        userClientLimit.setLabel("Maximum concurrent sessions for each user per keycloak client");
        userClientLimit.setType(ProviderConfigProperty.STRING_TYPE);
        userClientLimit.setDefaultValue(ONE_SESSION_ONLY);
        configProperties.add(userClientLimit);
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new UserSessionLimitAuthenticator(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
