package gov.uk.ets.registry.api.authz.miners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class MinedPolicy {
    public static final MinedRole ENROLLED_USER_ROLE = new MinedRole("enrolled-user");
    public static final MinedRole VALIDATED_USER_ROLE = new MinedRole("validated-user");
    public static final MinedRole REGISTERED_USER_ROLE = new MinedRole("registered-user");
    public static final EvaluationResult EMPTY_RESULT = new EvaluationResult("", "");
    @EqualsAndHashCode.Include
    private final String name;
    private final String logic; // POSITIVE, NEGATIVE
    private final String decisionStrategy; // UNANIMOUS, AFFIRMATIVE
    private final String type; // aggregate, scope, role, resource
    private List<MinedPolicy> applyPolicies = new ArrayList<>();
    private List<MinedRole> roles = new ArrayList<>();
    private List<MinedScope> scopes = new ArrayList<>();
    private List<MinedResource> resources = new ArrayList<>();

    private PolicyType policyType() {
        return PolicyType.of(type);
    }

    /**
     * Calculates the evaluation result after applying this policy to the scope.
     * @param parent the parent policy
     * @param minedScope the scope to check
     * @return the evaluation result
     */
    public EvaluationResult evaluationResultFromScope(MinedPolicy parent, MinedScope minedScope) {
        if (policyType().equals(PolicyType.AGGREGATE) && parent != null) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(),
                applyPolicies.stream()
                    .map(minedPolicy -> minedPolicy.evaluationResultFromScope(parent, minedScope))
                    .collect(Collectors.toList())
            );
        } else if (policyType().equals(PolicyType.ROLE) && parent != null &&
            parent.policyType().equals(PolicyType.SCOPE)) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(roles),
                new ArrayList<>());
        } else if (policyType().equals(PolicyType.SCOPE) && scopes.stream().anyMatch(s -> s.equals(minedScope))) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(),
                applyPolicies.stream()
                    .map(minedPolicy -> minedPolicy.evaluationResultFromScope(this, minedScope))
                    .collect(Collectors.toList())
            );
        }
        return getEvaluationResultForJsPolicies(parent, PolicyType.SCOPE);
    }

    /**
     * Calculates the evaluation result after applying this policy to the resource.
     * @param parent the parent policy
     * @param minedResource the scope to check
     * @return the evaluation result
     */
    public EvaluationResult evaluationResultFromResource(MinedPolicy parent, MinedResource minedResource) {
        if (policyType().equals(PolicyType.AGGREGATE) && parent != null) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(),
                applyPolicies.stream()
                    .map(minedPolicy -> minedPolicy.evaluationResultFromResource(parent, minedResource))
                    .collect(Collectors.toList())
            );
        } else if (policyType().equals(PolicyType.ROLE)
            && parent != null
            && parent.policyType().equals(PolicyType.RESOURCE)) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(roles),
                new ArrayList<>());

        } else if (policyType().equals(PolicyType.RESOURCE) &&
            resources.stream().anyMatch(r -> r.equals(minedResource))) {
            return new EvaluationResult(decisionStrategy, logic, new HashSet<>(),
                applyPolicies.stream()
                    .map(minedPolicy -> minedPolicy.evaluationResultFromResource(this, minedResource))
                    .collect(Collectors.toList())
            );
        }
        return getEvaluationResultForJsPolicies(parent, PolicyType.RESOURCE);
    }

    private EvaluationResult getEvaluationResultForJsPolicies(MinedPolicy parent, PolicyType policyType) {
        if (policyType().equals(PolicyType.ONLY_ENROLLED_POLICY) && parent != null &&
            parent.policyType().equals(policyType)) {
            return new EvaluationResult(decisionStrategy, logic, Set.of(ENROLLED_USER_ROLE), new ArrayList<>());
        } else if (policyType().equals(PolicyType.ONLY_VALIDATED_POLICY) && parent != null &&
            parent.policyType().equals(policyType)) {
            return new EvaluationResult(decisionStrategy, logic, Set.of(VALIDATED_USER_ROLE), new ArrayList<>());
        } else if (policyType().equals(PolicyType.ONLY_REGISTERED_POLICY) && parent != null &&
            parent.policyType().equals(policyType)) {
            return new EvaluationResult(decisionStrategy, logic, Set.of(REGISTERED_USER_ROLE), new ArrayList<>());
        }
        return EMPTY_RESULT;
    }


}
