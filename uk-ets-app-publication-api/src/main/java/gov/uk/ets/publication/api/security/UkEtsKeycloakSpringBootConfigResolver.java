package gov.uk.ets.publication.api.security;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Normally this class is not needed , due to a bug in keycloak thought it is
 * required.
 * UPDATE: This is still needed because of KEYCLOAK-9458 mentioned bellow.
 *
 * @author P35036
 * @see <a href=
 * "https://issues.jboss.org/browse/KEYCLOAK-11282?page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel&showAll=true">KEYCLOAK-11282</a>
 */
@Configuration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
public class UkEtsKeycloakSpringBootConfigResolver extends KeycloakSpringBootConfigResolver {

    @Autowired
    private KeycloakSpringBootProperties properties;

    private KeycloakDeployment keycloakDeployment;

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request facade) {
        if (keycloakDeployment != null) {
            return keycloakDeployment;
        }
        //Work around for bug https://issues.jboss.org/browse/KEYCLOAK-9458
        //This is just like setting keycloak.policy-enforcer-config.user-managed-access=true in application.properties
        properties.getPolicyEnforcerConfig().setUserManagedAccess(new PolicyEnforcerConfig.UserManagedAccessConfig());
        keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
        return keycloakDeployment;
    }
}

