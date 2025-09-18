package gov.uk.ets.registry.api.authz.miners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Keeps all authorization policies from the authz file.
 */
@Component
@Getter
public class ApiAuthzStore {

    Map<String, MinedScope> scopes = new HashMap<>();
    Map<String, MinedResource> resources = new HashMap<>();
    Map<String, MinedRole> roles = new HashMap<>();
    Map<String, MinedPolicy> policies = new HashMap<>();

    /**
     * Add a scope.
     * @param name scope name
     * @param minedScope the scope
     * @return the scope
     */
    public MinedScope addScope(@NotNull String name, @NotNull MinedScope minedScope) {
        if (!scopes.containsKey(name)) {
            scopes.put(name, minedScope);
        }
        return scopes.get(name);
    }

    /**
     * Add a resource.
     * @param url the url.
     * @param minedResource the resource
     * @return the resource
     */
    public MinedResource addResource(@NotNull String url, @NotNull MinedResource minedResource) {
        if (!resources.containsKey(url)) {
            resources.put(url, minedResource);
        }
        return resources.get(url);
    }

    /**
     * Add a role.
     * @param name the name
     * @param minedRole the role
     * @return the role
     */
    public MinedRole addRole(@NotNull String name, @NotNull MinedRole minedRole) {
        if (!roles.containsKey(name)) {
            roles.put(name, minedRole);
        }
        return roles.get(name);
    }

    /**
     * Add a policy.
     * @param name the name
     * @param minedPolicy the policy
     * @return the policy
     */
    public MinedPolicy addPolicy(@NotNull String name, @NotNull MinedPolicy minedPolicy) {
        if (!policies.containsKey(name)) {
            policies.put(name, minedPolicy);
        }
        return policies.get(name);
    }

    /**
     * Return a list of all {@link EvaluationResult} for a specific scope only if there are roles permitting the
     * scope.
     * @param minedScope the scope
     * @return a {@link List} of {@link EvaluationResult}
     */
    public List<EvaluationResult> getEvaluationResultByScope(MinedScope minedScope) {
        return policies.values().stream()
            .map(policy -> policy.evaluationResultFromScope(null, minedScope))
            .filter(o -> o.totalPermittingRoles() > 0)
            .collect(Collectors.toList());
    }

    /**
     * Get all {@link EvaluationResult} for a specific resource.
     * @param minedResource the resource
     * @return a {@link List} of {@link EvaluationResult}
     */
    public List<EvaluationResult> getEvaluationResultByResource(MinedResource minedResource) {
        return policies.values().stream()
            .map(policy -> policy.evaluationResultFromResource(null, minedResource))
            .filter(o -> o.totalPermittingRoles() > 0)
            .collect(Collectors.toList());
    }
}

