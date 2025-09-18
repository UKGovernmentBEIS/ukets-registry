package gov.uk.ets.keycloak.auth.password.policy.provider;

import java.io.IOException;

import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyNotMetException;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;

import gov.uk.ets.keycloak.auth.password.policy.factory.UkEtsPasswordPolicyProviderFactory;
import jakarta.ws.rs.ProcessingException;

public class UkEtsPasswordPolicyProvider implements PasswordPolicyProvider {

	private final KeycloakSession session;

	public UkEtsPasswordPolicyProvider(KeycloakSession session) {
		this.session = session;
	}
	
	public void close() {
	}

	public PolicyError validate(RealmModel realm, UserModel user, String password) {
		return validate(user.getUsername(), password);
	}

	public PolicyError validate(String user, String password) {
		String passwdValidateAPIServiceUrl = session.getContext().getRealm().getPasswordPolicy().getPolicyConfig(UkEtsPasswordPolicyProviderFactory.ID);
		try {	
			ValidatePasswordResponse response = SimpleHttp
					.doPost(passwdValidateAPIServiceUrl, session)
					.json(password)
					.asJson(ValidatePasswordResponse.class);
			
			if(!response.isValid()) {
				throw new PasswordPolicyNotMetException(user, response.getMessage());
			}
		} catch (ProcessingException e) {
			return new PolicyError(e.getMessage());
		} catch (IOException e) {
			return new PolicyError(e.getMessage());
		}
		
		return null;
	}

	public Object parseConfig(String passwdValidateAPIServiceUrl) {
		return passwdValidateAPIServiceUrl;
	}
}
