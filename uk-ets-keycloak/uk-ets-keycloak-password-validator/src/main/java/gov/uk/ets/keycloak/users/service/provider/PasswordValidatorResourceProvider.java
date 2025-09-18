package gov.uk.ets.keycloak.users.service.provider;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

import org.jboss.resteasy.reactive.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

public class PasswordValidatorResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public PasswordValidatorResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        // NOOP
    }

    @POST
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validatePassword(final PasswordValidationRequest request) {
        Boolean answer = Boolean.FALSE;
        Optional<UserModel> userModel = getAuthenticatedUser();
        if (userModel.isPresent()) {
            answer = validate(request.getPassword(), userModel.get());
        }

        return Response.ok()
            .entity(answer)
            .build();
    }

    private Boolean validate(String password, UserModel userModel) {
        return userModel.credentialManager().isValid(UserCredentialModel.password(password));
    }

    private Optional<UserModel> getAuthenticatedUser() {
        AppAuthManager.BearerTokenAuthenticator  authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
        AuthenticationManager.AuthResult authResult =  authenticator.authenticate();

        if (authResult != null) {
            return Optional.of(authResult.getUser());
        }

        return Optional.empty();
    }

}
