package gov.uk.ets.keycloak.authentication.authenticators.browser;

import java.util.concurrent.TimeUnit;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.events.Errors;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.representations.idm.CredentialRepresentation;

public class UkEtsAuthenticator extends UsernamePasswordForm {

	public static final String PASSWD_BLACKLISTED = "passwdBlacklistedMessage";
	public static final String PASSWD_NOT_COMPLYING_POLICY = "passwdNotComplyingPolicyMessage";
	public static final String PASSWD_MISSING = "invalidUserMessage";

	private static final String PASSWD_POLICY_VIOLATION = "password_policy_violation";
	private static final String PASSWD_PWNED = "pwned";

	private Client client = ClientBuilder.newBuilder().
			connectTimeout(1, TimeUnit.SECONDS).
			readTimeout(1, TimeUnit.SECONDS).
			build();

	@Override
	public void action(AuthenticationFlowContext context) {
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		String password = formData.getFirst(CredentialRepresentation.PASSWORD);

		String passwdValidateAPIServiceUrl = new StringBuilder().append("http://")
				.append(System.getenv("PASSWORD_VALIDATOR"))
				.append(":")
				.append("8096")
				.append("/api-password-validate")
				.toString();

		AuthenticatorConfigModel config = context.getAuthenticatorConfig();

		if (config != null) {
			passwdValidateAPIServiceUrl = config.getConfig().get("password.validator.service.url");
		}

		if (password.isEmpty()) {
			emptyPasswordHandler(context);
			return;
		}
		
		if(!validateUserAndPassword(context, formData)) {
	        return;
		}

		try {
			ValidatePasswordResponse response = client.target(passwdValidateAPIServiceUrl)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(password, MediaType.TEXT_PLAIN), ValidatePasswordResponse.class);

			if (!response.isValid()) {
				if (PASSWD_POLICY_VIOLATION.equals(response.getErrorCode())) {
					passwordPolicyViolationHandler(context);
					return;
				}
				if (PASSWD_PWNED.equals(response.getErrorCode())) {
					blacklistedPasswordHandler(context);
					return;
				}
			}
		} catch (ProcessingException e) {
			// Do nothing.
		}
		super.action(context);
	}

	private boolean blacklistedPasswordHandler(AuthenticationFlowContext context) {
		context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
		Response challengeResponse = challenge(context, PASSWD_BLACKLISTED);
		context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);

		return false;
	}

	private void passwordPolicyViolationHandler(AuthenticationFlowContext context) {
		context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
		Response challengeResponse = challenge(context, PASSWD_NOT_COMPLYING_POLICY);
		context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
	}

	private boolean emptyPasswordHandler(AuthenticationFlowContext context) {
		context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
		Response challengeResponse = challenge(context, PASSWD_MISSING);
		context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);

		return false;
	}
}
