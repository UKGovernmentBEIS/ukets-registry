package gov.uk.ets.registry.api.authz.miners;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * This class will evaluate any {@link MinedResource} and {@link MinedScope} against all policies found in the
 * {@link ApiAuthzStore}.
 */
@RequiredArgsConstructor
@Log4j2
public class Evaluator {

    private final ApiAuthzStore policyStore;

    /**
     * Returns the evaluation representation for the resource.
     *
     * @param resource the resource
     * @return the evaluation representation
     */
    public Set<ResourceScopeEvaluationRepresentation> evaluate(MinedResource resource) {
        if (resource.getScopes().isEmpty()) {
            return evaluateResource(resource).stream()
                .map(evaluationResult ->
                    new ResourceScopeEvaluationRepresentation(resource, MinedScope.NO_SCOPE,
                        evaluationResult.getDecisionStrategy(), evaluationResult.getLogic(),
                        evaluationResult.represent())).collect(Collectors.toSet());

        }
        return resource.getScopes()
            .stream()
            .flatMap(scope -> evaluateScope(scope).stream()
                .map(evaluationResult ->
                    new ResourceScopeEvaluationRepresentation(resource, scope,
                        evaluationResult.getDecisionStrategy(), evaluationResult.getLogic(),
                        evaluationResult.represent()))
            )
            .collect(Collectors.toSet());
    }

    private List<EvaluationResult> evaluateScope(MinedScope minedScope) {
        return policyStore.getEvaluationResultByScope(minedScope);
    }

    private List<EvaluationResult> evaluateResource(MinedResource minedResource) {
        return policyStore.getEvaluationResultByResource(minedResource);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @ToString
    @Getter
    public static class ResourceScopeEvaluationRepresentation {
        private final MinedResource resource;
        private final MinedScope scope;
        private final String decisionStrategy;
        private final String logic;
        private final String evaluationRepresentation;
    }
}
