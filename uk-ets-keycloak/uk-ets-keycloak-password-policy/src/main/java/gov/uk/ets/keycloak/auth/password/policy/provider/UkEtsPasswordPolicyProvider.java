package gov.uk.ets.keycloak.auth.password.policy.provider;

import gov.uk.ets.keycloak.auth.password.policy.factory.UkEtsPasswordPolicyProviderFactory;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;

public class UkEtsPasswordPolicyProvider implements PasswordPolicyProvider {

	private Client client = ClientBuilder.newClient();
	private final KeycloakContext context;

	public UkEtsPasswordPolicyProvider(KeycloakContext context) {
		this.context = context;
	}
	
	public void close() {
	}

	public PolicyError validate(RealmModel realm, UserModel user, String password) {
		return validate(user.getUsername(), password);
	}

	public PolicyError validate(String user, String password) {
		String passwdValidateAPIServiceUrl = context.getRealm().getPasswordPolicy().getPolicyConfig(UkEtsPasswordPolicyProviderFactory.ID);
		try {	
			
			ValidatePasswordResponse response = client
			          .target(passwdValidateAPIServiceUrl)
			          .request(MediaType.APPLICATION_JSON)
			          .post(Entity.entity(password, MediaType.TEXT_PLAIN),ValidatePasswordResponse.class);
			
			if(!response.isValid()) {
				return new PolicyError(response.getErrorCode(), response.getMessage());
			}
		} catch (ProcessingException e) {
			return new PolicyError(e.getMessage());
		}
		
		return null;
	}

	public Object parseConfig(String passwdValidateAPIServiceUrl) {
		return passwdValidateAPIServiceUrl;
	}
}
