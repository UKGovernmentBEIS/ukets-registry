package gov.uk.ets.keycloak.recovery.rest;

import gov.uk.ets.keycloak.recovery.dto.SecurityCodeDTO;
import gov.uk.ets.keycloak.recovery.spi.SecurityCodeService;
import gov.uk.ets.keycloak.recovery.spi.TokenService;
import gov.uk.ets.keycloak.recovery.spi.impl.SecurityCodeServiceImpl;
import gov.uk.ets.keycloak.recovery.spi.impl.TokenServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.List;

public class SecurityCodeManagerResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    private final SecurityCodeService service;
    private final TokenService tokenService;

    public SecurityCodeManagerResourceProvider(KeycloakSession session) {
        this.session = session;
        this.tokenService = new TokenServiceImpl(session);
        this.service = new SecurityCodeServiceImpl(session.getProvider(JpaConnectionProvider.class).getEntityManager());
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        // NOOP
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response retrieveSecurityCodes(@QueryParam("userId") String userId) {
        checkPermissions();
        List<SecurityCodeDTO> securityCode = service.findSecurityCodesByUserId(userId);
        return Response.ok(securityCode).build();
    }

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestSecurityCode(final SecurityCodeRequest request) {
        checkPermissions();
        SecurityCodeDTO securityCodeDTO;
        if (request.getEmail() != null) {
            securityCodeDTO = service.requestSecurityCode(request.getUserId(), request.getEmail());
        } else {
            securityCodeDTO = service.requestSecurityCode(request.getUserId(), request.getCountryCode(), request.getPhoneNumber());
        }
        return Response.ok(securityCodeDTO).build();
    }

    @POST
    @Path("/attempt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAttempt(@QueryParam("userId") String userId) {
        checkPermissions();
        SecurityCodeDTO securityCode = service.addAttempt(userId);
        return Response.ok(securityCode).build();
    }

    @GET
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateResetToken(@QueryParam("userId") String userId) {
        checkPermissions();
        String token = tokenService.generateResetPasswordToken(userId);
        return Response.ok(new TokenResponse(token)).build();
    }

    private void checkPermissions() {
    	AppAuthManager.BearerTokenAuthenticator authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
    	AuthenticationManager.AuthResult auth = authenticator.authenticate();

        if (auth == null ||
            auth.getToken().getResourceAccess() == null ||
            auth.getToken().getResourceAccess().get("realm-management") == null) {
            throw new NotAuthorizedException("Bearer");
        }
        AccessToken.Access realmMgmt = auth.getToken().getResourceAccess().get("realm-management");
        if(!realmMgmt.getRoles().contains("query-users")) {
            throw new ForbiddenException();
        }
    }
}
