package gov.uk.ets.registry.api.authz;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import java.util.Set;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse.EvaluationResultRepresentation;

/**
 * Service for Authorizations.
 *
 * @author P35036
 */
public interface AuthorizationService extends AccessTokenRetriever {

    /**
     * Returns true of the user the role.
     *
     * @param role the user role
     * @return true if there is a client role
     */
    boolean hasClientRole(UserRole role);

    boolean hasScopePermission(Scope scope);

    /**
     * Gets client level roles.
     *
     * @param iamIdentifier the identifier of the user in keycloak
     * @return a list of roles
     */
    List<RoleRepresentation> getClientLevelRoles(String iamIdentifier);

    /**
     * Returns the scopes.
     *
     * @return the scopes
     */
    Set<String> getScopes();

    /**
     * Evaluates the policy request.
     *
     * @param policyEvaluationRequest the evaluation policy request
     * @return the evaluation result
     */
    EvaluationResultRepresentation evaluate(PolicyEvaluationRequest policyEvaluationRequest);

    /**
     * Access token as a string.
     *
     * @return the access token as a String
     * @since v.9.0
     */
    String getTokenString();

    /**
     *  Get user Urid.
     *  @return the urid as a String
     */
    String getUrid();

    /**
     * Gets the value of the claim from the token.
     * @param name
     * @return
     */
    String getClaim(OAuth2ClaimNames name);

	boolean isLoggedIn();
}
