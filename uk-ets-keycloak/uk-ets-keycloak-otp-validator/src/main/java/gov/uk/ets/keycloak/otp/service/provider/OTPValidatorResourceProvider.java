package gov.uk.ets.keycloak.otp.service.provider;

import java.util.Optional;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.NoCache;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.OTPCredentialProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.services.resource.RealmResourceProvider;

public class OTPValidatorResourceProvider implements RealmResourceProvider {

    private KeycloakSession session;
    private BruteForceProtector protector;

    public OTPValidatorResourceProvider(KeycloakSession session) {
        this.session = session;
        this.protector = session.getProvider(BruteForceProtector.class);
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
    @Consumes("application/x-www-form-urlencoded")
    public Response validateOTP(@FormParam("otp") String otp) {
        Boolean answer = Boolean.FALSE;
        Optional<UserModel> userModel = getAuthenticatedUser();
        if(userModel.isPresent()) {
            answer = validate(otp,userModel.get());
        }

       return Response.ok()
           .entity(answer)
           .build();
    }

    /**
     * Validates an OTP without a bearer token in the request.
     * @param otp
     * @param email
     * @return
     */
    @POST
    @Path("/nobearer")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response validateOTPNoBearerToken(@FormParam("otp") String otp,@FormParam("email") String email) {
        Boolean answer = Boolean.FALSE;
        Optional<UserModel> userModel = getUserByEmail(email);
        if(userModel.isPresent()) {
            answer = validate(otp,userModel.get());
        }

       return Response.ok()
           .entity(answer)
           .build();
    }
    
    public OTPCredentialProvider getCredentialProvider(KeycloakSession session) {
        return (OTPCredentialProvider)session.getProvider(CredentialProvider.class, "keycloak-otp");
    }
    
    private Boolean validate(String otp,UserModel userModel) {
        OTPCredentialProvider credentialProvider = getCredentialProvider(session);
        RealmModel realmModel = session.getContext().getRealm();
        OTPCredentialModel preferredCredential = credentialProvider.getDefaultCredential(session,realmModel, userModel);

        if(Optional.ofNullable(preferredCredential).isEmpty()) {
            //User has not set up 2FA
            return Boolean.FALSE;
        }
        
        if(isTemporarilyDisabledByBruteForce(userModel)) {
            return Boolean.FALSE;
        }
        
        //Check the OTP
        boolean otpValid = credentialProvider.isValid(realmModel, userModel,new UserCredentialModel(preferredCredential.getId(), credentialProvider.getType(), otp));
        
        if(!otpValid) {
        	//Accounts should be disabled when multiple incorrect 2FA codes are attempted in succession.
        	increaseBruteForceProtectorCounter(userModel);
        } else {
        	//a valid 2FA code has subsequently been entered within the allowed incorrect count range, 
        	//the incorrect 2FA count should be reset to zero
        	resetBruteForceProtectorCounter(userModel);
        }
        
        return otpValid;
    }    
    
    private Optional<UserModel> getAuthenticatedUser() {
    	AppAuthManager.BearerTokenAuthenticator  authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
    	AuthenticationManager.AuthResult authResult =  authenticator.authenticate();

        if (authResult != null) {
            return Optional.of(authResult.getUser());
        }
        
        return Optional.empty();
    }
    
    private Optional<UserModel> getUserByEmail(String email) {
        UserModel user = session.users().getUserByEmail(session.getContext().getRealm(), email);
        if (user != null) {
            return Optional.of(user);
        }
        
        return Optional.empty();
    }
    
    /**
     * Records a failed otp validation as a login failure to the BruteForceProtector. 
     * @param user
     */
    private void increaseBruteForceProtectorCounter(UserModel user) {
    	RealmModel realm = session.getContext().getRealm();
        if (realm.isBruteForceProtected()) {
            if (user != null) {
                protector.failedLogin(realm, user, session.getContext().getConnection(), session.getContext().getUri());
            }
        }
    }
    
    /**
     * Resets BruteForceProtector upon succesfull otp validation. 
     * @param user
     */
    private void resetBruteForceProtectorCounter(UserModel user) {
    	RealmModel realm = session.getContext().getRealm();
        if (realm.isBruteForceProtected()) {
            if (user != null) {
                protector.successfulLogin(realm, user, session.getContext().getConnection(), session.getContext().getUri());
            }
        }
    }
    
    /**
     * Checks if the specified User is locked due to brute force protection..
     * @param user
     * @return
     */
    protected boolean isTemporarilyDisabledByBruteForce(UserModel user) {
    	RealmModel realm = session.getContext().getRealm();
        if (realm.isBruteForceProtected()) {
            if (protector.isTemporarilyDisabled(session, realm, user)) {
                return true;
            }
        }
        return false;
    }
}
