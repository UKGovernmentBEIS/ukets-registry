package gov.uk.ets.registry.api.authz;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse.EvaluationResultRepresentation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class DisabledKeycloakAuthorizationService implements AuthorizationService {

    @Override
    public EvaluationResultRepresentation evaluate(PolicyEvaluationRequest policyEvaluationRequest) {
        EvaluationResultRepresentation result = new EvaluationResultRepresentation();
        result.setStatus(DecisionEffect.PERMIT);
        return result;
    }

    @Override
    public String getTokenString() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String getUrid() {
        return "";
    }

    @Override
    public AccessTokenResponse obtainAccessToken() {
        // TODO Implement method
        return new AccessTokenResponse();
    }

    @Override
    public boolean hasClientRole(UserRole role) {
        return true;
    }

    @Override
    public List<RoleRepresentation> getClientLevelRoles(String iamIdentifier) {
        // TODO Implement method
        return new ArrayList<RoleRepresentation>();
    }

    @Override
    public Set<String> getScopes() {
        return Stream.of(Scope.values()).map(t -> t.getScopeName()).collect(Collectors.toSet());
    }

    @Override
    public boolean hasScopePermission(Scope scope) {
        return true;
    }

    @Override
    public String getClaim(OAuth2ClaimNames name) {
        return "";
    }
}
