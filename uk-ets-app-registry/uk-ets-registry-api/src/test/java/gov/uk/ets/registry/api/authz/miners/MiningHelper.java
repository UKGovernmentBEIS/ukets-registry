package gov.uk.ets.registry.api.authz.miners;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

public class MiningHelper {

    public ResourceServerRepresentation resourceServerRepresentation() {
        ResourceServerRepresentation resourceServerRepresentation = new ResourceServerRepresentation();
        resourceServerRepresentation.setPolicies(representations());
        return resourceServerRepresentation;
    }

    List<PolicyRepresentation> representations() {
        PolicyRepresentation policyRepresentation = new PolicyRepresentation();
        return List.of(rolePolicy("1"));
    }

    PolicyRepresentation rolePolicy(String id) {
        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setType("role");
        policy.setName("Role policy " + id);
        policy.setLogic(Logic.POSITIVE);
        policy.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
        policy.setConfig(Map.of("roles", "[{\"id\":\"uk-ets-registry-api/readonly-administrator\",\"required\":true}]"));
        return policy;
    }

    public static List<MinedResource> minedResource() {
        return List.of(new MinedResource("resource-1", Set.of(), List.of()));
    }

    public static List<MinedScope> minedScope() {
        return List.of(new MinedScope("scope-1"));
    }

    public static List<MinedRole> minedRole() {
        return List.of(new MinedRole("role-1"));
    }

    public static List<MinedResource> minedResources() {
        return List.of(new MinedResource("resource-1", Set.of(), List.of()),
            new MinedResource("resource-2", Set.of(), List.of()),
            new MinedResource("resource-3", Set.of(), List.of()));
    }

    public static List<MinedScope> minedScopes() {
        return List.of(new MinedScope("scope-1"),
            new MinedScope("scope-2"),
            new MinedScope("scope-3"));
    }

    public static List<MinedRole> minedRoles() {
        return List.of(new MinedRole("role-1"),
            new MinedRole("role-2"),
            new MinedRole("role-3"));
    }
}
