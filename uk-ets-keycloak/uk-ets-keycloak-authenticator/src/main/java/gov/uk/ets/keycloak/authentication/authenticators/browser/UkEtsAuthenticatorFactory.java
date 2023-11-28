package gov.uk.ets.keycloak.authentication.authenticators.browser;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

public class UkEtsAuthenticatorFactory extends UsernamePasswordFormFactory {

    public static final String PROVIDER_ID = "uk-ets-auth-username-password-form";
    public static final UkEtsAuthenticator SINGLETON = new UkEtsAuthenticator();

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }

    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "UK ETS Username Password Form";
    }

    @Override
    public String getHelpText() {
        return "Validates a username and password from login form and checks for blacklisted password in an external service.";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName("password.validator.service.url");
        property.setLabel("Password Validator Service URL");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The url of the external service that checks the password.");
        configProperties.add(property);
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

}
