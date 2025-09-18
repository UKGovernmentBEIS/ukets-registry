package gov.uk.ets.keycloak.recovery.action;

import gov.uk.ets.keycloak.recovery.spi.impl.SecurityCodeServiceImpl;
import jakarta.persistence.EntityManager;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SecurityCodeCleanUpActionProvider implements RequiredActionProvider, RequiredActionFactory {

    private static final String ID = "cleanup-security-code";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        EntityManager entityManager = context.getSession().getProvider(JpaConnectionProvider.class).getEntityManager();
        String userId = context.getUser().getId();

        new SecurityCodeServiceImpl(entityManager).clearSecurityCodes(userId);
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        // Nothing to do
    }

    @Override
    public void processAction(RequiredActionContext context) {
        context.success();
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new SecurityCodeCleanUpActionProvider();
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to do
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayText() {
        return "UK ETS User clean up security codes";
    }
}
