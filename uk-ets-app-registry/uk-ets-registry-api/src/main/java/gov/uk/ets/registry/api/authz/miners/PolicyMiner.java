package gov.uk.ets.registry.api.authz.miners;


import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

/**
 * Responsible for mining a policy out of from a {@link ResourceServerRepresentation}.
 */
@RequiredArgsConstructor
@Getter
public class PolicyMiner implements GenericMiner, RoleMiner {
    private final ResourceServerRepresentation resourceServerRepresentation;
    private final ApiAuthzStore policyStore;
    private final ScopeMiner scopeMiner;

    /**
     * Return the policies.
     *
     * @return the policies
     */
    public List<MinedPolicy> policies() {
        return resourceServerRepresentation
            .getPolicies()
            .stream()
            .map(this::from)
            .collect(Collectors.toList());
    }

    private MinedPolicy from(PolicyRepresentation policyRepresentation) {
        return MinedPolicy
            .builder()
            .name(policyRepresentation.getName())
            .logic(policyRepresentation.getLogic().name())
            .type(policyRepresentation.getType())
            .decisionStrategy(policyRepresentation.getDecisionStrategy().name())
            .applyPolicies(applyPolicies(policyRepresentation))
            .roles(roles(policyRepresentation))
            .resources(resources(policyRepresentation))
            .scopes(scopes(policyRepresentation))
            .build();
    }

    private MinedResource from(ResourceRepresentation resourceRepresentation) {
        List<MinedScope> minedScopes = scopeMiner.scopes(resourceRepresentation.getName());
        return new MinedResource(resourceRepresentation.getName(), resourceRepresentation.getUris(), minedScopes);
    }

    private List<MinedScope> scopes(PolicyRepresentation policyRepresentation) {
        List<String> scopes = mine(policyRepresentation.getConfig(), "scopes");
        return scopes.stream().map(this::loadScope).collect(Collectors.toList());

    }

    private List<MinedResource> resources(PolicyRepresentation policyRepresentation) {
        List<String> resources = mine(policyRepresentation.getConfig(), "resources");
        return resources.stream().map(this::loadResource).collect(Collectors.toList());

    }

    private List<MinedRole> roles(PolicyRepresentation policyRepresentation) {
        List<String> roles = mineRole(policyRepresentation.getConfig());
        return roles.stream().map(this::loadRole).collect(Collectors.toList());
    }

    private List<MinedPolicy> applyPolicies(PolicyRepresentation policyRepresentation) {
        List<String> policies = mine(policyRepresentation.getConfig(), "applyPolicies");
        return policies.stream().map(this::loadPolicy).collect(Collectors.toList());
    }

    private MinedPolicy loadPolicy(String name) {
        MinedPolicy minedPolicy = resourceServerRepresentation
            .getPolicies()
            .stream()
            .filter(p -> p.getName().equals(name))
            .map(this::from)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(name));
        return policyStore.addPolicy(name, minedPolicy);
    }

    private MinedRole loadRole(String name) {
        return policyStore.addRole(name, new MinedRole(name));
    }

    private MinedScope loadScope(String name) {
        return policyStore.addScope(name, new MinedScope(name));
    }

    private MinedResource loadResource(String name) {
        MinedResource minedResource = resourceServerRepresentation
            .getResources()
            .stream().filter(r -> r.getName().equals(name))
            .map(this::from)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(name));
        return policyStore.addResource(name, minedResource);
    }

}
