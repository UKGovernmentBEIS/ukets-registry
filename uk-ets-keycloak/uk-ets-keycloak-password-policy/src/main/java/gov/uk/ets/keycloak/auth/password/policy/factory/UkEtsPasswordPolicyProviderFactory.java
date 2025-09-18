package gov.uk.ets.keycloak.auth.password.policy.factory;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

import gov.uk.ets.keycloak.auth.password.policy.provider.UkEtsPasswordPolicyProvider;

public class UkEtsPasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {

    public static final String ID = "uk-ets-password-policy";
	
	public PasswordPolicyProvider create(KeycloakSession session) {
		return new UkEtsPasswordPolicyProvider(session);
	}

	public void init(Scope config) {
		
	}

	public void postInit(KeycloakSessionFactory factory) {
		
	}

	public void close() {
		
	}

	public String getId() {
		return ID;
	}

	public String getDisplayName() {
		return "UK ETS Policy";
	}

	public boolean isMultiplSupported() {
		return false;
	}

	public String getConfigType() {
		return PasswordPolicyProvider.STRING_CONFIG_TYPE;
	}

	public String getDefaultConfigValue() {
		StringBuilder defaultConfigurationBuilder = new StringBuilder()
			.append("http://")
			.append(System.getenv("PASSWORD_VALIDATOR"))
			.append(":")
			.append("8096")
			.append("/api-password-validate");
		return defaultConfigurationBuilder.toString();
	}
	
}
