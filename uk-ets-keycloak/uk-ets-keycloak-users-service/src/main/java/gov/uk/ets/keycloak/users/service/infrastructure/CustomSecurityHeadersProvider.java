package gov.uk.ets.keycloak.users.service.infrastructure;

import gov.uk.ets.keycloak.logger.CustomLogger;
import org.jboss.logging.Logger;
import org.keycloak.headers.DefaultSecurityHeadersProvider;
import org.keycloak.headers.SecurityHeadersOptions;
import org.keycloak.models.KeycloakSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

/**
 * This overrides {@link org.keycloak.headers.DefaultSecurityHeadersProvider} in order to record logs with the
 * interaction identifier (X-Request-ID) provided from the Registry.
 * See {@link CustomSecurityHeadersProvider#addLogging(ContainerRequestContext, ContainerResponseContext)}
 */
public class CustomSecurityHeadersProvider extends DefaultSecurityHeadersProvider {

    public CustomSecurityHeadersProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    public SecurityHeadersOptions options() {
        return super.options();
    }

    @Override
    public void addHeaders(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        addLogging(requestContext,responseContext);
        super.addHeaders(requestContext,responseContext);
    }

    private void addLogging(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String headerString = requestContext.getHeaderString("X-Request-ID");
        Logger.Level loggerLevel = (requestContext.getUriInfo().getAbsolutePath().toString().endsWith("/token")
                || requestContext.getUriInfo().getAbsolutePath().toString().endsWith("/uma2-configuration")
                || headerString == null)
                ? Logger.Level.DEBUG : Logger.Level.INFO;
        CustomLogger.print(loggerLevel, headerString, requestContext.getUriInfo().getAbsolutePath().getPath(),
                String.format("%s %s %s", responseContext.getStatusInfo().getFamily(),
                responseContext.getStatusInfo().getStatusCode(), responseContext.getStatusInfo().getReasonPhrase()),
                null, null, null);
    }
}
