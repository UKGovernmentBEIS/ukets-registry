package gov.uk.ets.keycloak.recovery.spi.impl;

import gov.uk.ets.keycloak.recovery.spi.TokenService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.keycloak.authentication.actiontoken.execactions.ExecuteActionsActionToken;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.google.common.primitives.Ints;

public class TokenServiceImpl implements TokenService {

    /** How long the action token is valid in minutes.*/
	public static final int ACTION_TOKEN_VALIDITY = 5;
    private static final String DEFAULT_CLIENT_ID = "uk-netz-api-account-recovery";
    private final KeycloakSession session;

    public TokenServiceImpl(KeycloakSession session) {
        this.session = session;
    }

    public String generateResetPasswordToken(String userId) {

        RealmModel realm = session.getContext().getRealm();
        UserModel userModel = session.users().getUserById(realm, userId);

        // Step 1: Define necessary variables for user and client identification
        String email = userModel.getEmail();
        String clientId = Optional.ofNullable(session.getContext().getClient())
            .map(ClientModel::getClientId)
            .orElse(DEFAULT_CLIENT_ID);

        //By JWT specification (RFC 7519) expiration is in seconds.
        long expiration = LocalDateTime.now().plusMinutes(ACTION_TOKEN_VALIDITY).toEpochSecond(ZoneOffset.UTC);
        List<String> actions = Arrays.asList("UPDATE_PASSWORD");

        // Step 2: Create the action token for password reset
        // The conversion of long to int is mandated by Keycloak codebase.
        ExecuteActionsActionToken
            token = new ExecuteActionsActionToken(userId, email, Ints.saturatedCast(expiration), actions, null, clientId);
        //Set the correct expiration value as long here
        //just in case.
        token.exp(expiration);
        
        return token.serialize(session, realm, session.getContext().getUri());
    }
}
