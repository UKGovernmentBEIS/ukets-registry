package gov.uk.ets.keycloak.migration.rest;

import gov.uk.ets.keycloak.migration.jpa.Migration;
import gov.uk.ets.keycloak.migration.spi.MigrationService;
import gov.uk.ets.keycloak.migration.spi.impl.MigrationServiceImpl;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

public class MigrationManagerResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    private final MigrationService service;

    public MigrationManagerResourceProvider(KeycloakSession session) {
        this.session = session;
        service = new MigrationServiceImpl(session);
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        // NOOP
    }

    /**
     * Retrieve migration entity by it key.
     */
    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMigrationByKey(@QueryParam("key") String key) {
        checkRealmAdmin();
        Migration migration = service.findByKey(key);
        if (migration == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(migration).build();
    }

    /**
     * Create new migration entry using the provided key and a current timestamp.
     */
    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveMigration(final MigrationRequest request) {
        checkRealmAdmin();
        Migration migration = service.addMigration(request.getKey());
        return Response.ok().entity(migration).build();
    }

    private void checkRealmAdmin() {
        RealmModel currentRealm = session.getContext().getRealm();
    	AppAuthManager.BearerTokenAuthenticator  authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
    	AuthenticationManager.AuthResult auth =  authenticator.authenticate();

        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        } else if (!auth.getUser().hasRole(KeycloakModelUtils.getRoleFromString(currentRealm, "admin"))) {
            throw new ForbiddenException("Does not have realm admin role");
        }
    }
}
