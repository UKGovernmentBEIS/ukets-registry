package gov.uk.ets.keycloak.users.service.infrastructure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;

/**
 * Keycloak extension for recording the last sign in date into a user attribute.
 */
public class UkEtsUserLastLoginRecorder implements RequiredActionProvider, RequiredActionFactory {
    /**
     * Store the current date and time as the last login date for the current user
     * @param context
     */
    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        String lastLoginDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        UserModel user = context.getUser();
        user.setAttribute(Constants.LAST_SIGN_IN_DATE, List.of(lastLoginDate));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAction(RequiredActionContext context) {
        context.success();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new UkEtsUserLastLoginRecorder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Scope config) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return Constants.LAST_LOGIN_DATE_RECORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayText() {
        return Constants.LAST_LOGIN_DATE_RECORDER_DISPLAY_TEXT;
    }
}
