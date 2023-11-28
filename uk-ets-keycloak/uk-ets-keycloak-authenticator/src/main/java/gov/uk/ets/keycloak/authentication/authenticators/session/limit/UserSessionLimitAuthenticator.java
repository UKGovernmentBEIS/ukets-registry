package gov.uk.ets.keycloak.authentication.authenticators.session.limit;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.services.managers.AuthenticationManager;

public class UserSessionLimitAuthenticator implements Authenticator {

    private static Logger logger = Logger.getLogger(UserSessionLimitAuthenticator.class);
    private KeycloakSession session;
    private EventBuilder event;
    
    public UserSessionLimitAuthenticator(KeycloakSession session) {
        this.session = session;
        event = new EventBuilder(session.getContext().getRealm(), session, session.getContext().getConnection());
    }
	
	@Override
	public void close() {
		
	}

	//See also https://github.com/mfdewit/keycloak/tree/KEYCLOAK-849-configurable-session-limits/services/src/main/java/org/keycloak/authentication/authenticators/sessionlimits
	
	@Override
	public void authenticate(AuthenticationFlowContext context) {
        AuthenticatorConfigModel authenticatorConfig = context.getAuthenticatorConfig();
        int userClientLimit = UserSessionLimitAuthenticatorFactory.ONE_SESSION_ONLY;
        if(Optional.ofNullable(authenticatorConfig).isPresent()) {
            Map<String, String> config = authenticatorConfig.getConfig();
            // Get the configuration for this authenticator
            userClientLimit = getIntConfigProperty(UserSessionLimitAuthenticatorFactory.USER_CLIENT_LIMIT, config);	
        }


        if (context.getRealm() != null && context.getUser() != null) {

            // Get the session count related to the current client for this user
            ClientModel currentClient = context.getAuthenticationSession().getClient();
            logger.infof("session-limiter's current keycloak clientId: %s", currentClient.getClientId());

            // Get the session count in this realm for this specific user
            Stream<UserSessionModel> userSessionsForRealmStream = session.sessions().getUserSessionsStream(context.getRealm(), context.getUser());

            List<UserSessionModel> userSessionsForClient = userSessionsForRealmStream.filter(userSession -> userSession.getAuthenticatedClientSessionByClient(currentClient.getId()) != null).collect(toList());
            int userSessionCountForClient = userSessionsForClient.size();
            logger.infof("session-limiter's configured client session limit: %s", userClientLimit);
            logger.infof("session-limiter's count of total user sessions for this keycloak client: %s", userSessionCountForClient);

            // First check if the user has too many sessions in this realm
            if (exceedsLimit(userSessionCountForClient, userClientLimit)) {
                logger.info("Too many sessions related to the current client for this user.");
                handleLimitExceeded(context, userSessionsForClient);
                event.event(EventType.LOGOUT);
                event.user(context.getUser());
                event.client(currentClient);
                event.detail("Forced Logout.","Session Limit exceeded for user.");
                event.success();
            } else {
                context.success();
            }
        } else {
            context.success();
        }
		
	}

    protected int getIntConfigProperty(String key, Map<String, String> config) {
        String value = config.get(key);
        if (Optional.ofNullable(value).isEmpty() || value.length() == 0) {
            return UserSessionLimitAuthenticatorFactory.ONE_SESSION_ONLY;
        }
        return Integer.parseInt(value);
    }	
	
    protected boolean exceedsLimit(long count, long limit) {
        if (limit < 0) { // if limit is negative, no valid limit configuration is found
            return false;
        }
        return count > limit - 1;
    }	
	
    private void handleLimitExceeded(AuthenticationFlowContext context, List<UserSessionModel> userSessions) {
        logger.info("Terminating oldest session");
        logoutOldestSession(userSessions);
        context.success();       
    }

    private void logoutOldestSession(List<UserSessionModel> userSessions) {
        logger.info("Logging out oldest session");
        Optional<UserSessionModel> oldest = userSessions.stream().sorted(Comparator.comparingInt(UserSessionModel::getStarted)).findFirst();
        oldest.ifPresent(userSession -> AuthenticationManager.backchannelLogout(session, userSession, true));
    }
	
	@Override
	public void action(AuthenticationFlowContext context) {
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm,UserModel user) {
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm,UserModel user) {
	}

}
