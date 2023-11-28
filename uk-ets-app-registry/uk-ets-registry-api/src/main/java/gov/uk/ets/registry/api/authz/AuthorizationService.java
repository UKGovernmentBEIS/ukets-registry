package gov.uk.ets.registry.api.authz;

import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import java.util.Set;
import org.keycloak.representations.AccessToken;
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
     * Return the Access token of the currently logged in user.
     *
     * @return the Access token of the currently logged in user
     * @since v0.3.0
     */
    AccessToken getToken();

    /**
     * Access token as a string.
     *
     * @return the access token as a String
     * @since v.9.0
     */
    String getTokenString();

    /**
     *  Get user Urid
     *  @return the urid as a String
     */
    String getUrid();
}
