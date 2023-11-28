package gov.uk.ets.registry.api.authz.miners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This class stores the result of an evaluation. Each evaluation can hold zero or more child results.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationResult {
    final String decisionStrategy;
    final String logic;
    final EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(this);

    Set<MinedRole> roles = new HashSet<>();
    List<EvaluationResult> childEvaluationResults = new ArrayList<>();

    /**
     * What is the total number of roles that relate to these evaluation result.
     * @return the number of permitting roles.
     */
    public int totalPermittingRoles() {
        return roles.size()
            + childEvaluationResults.stream()
                .map(EvaluationResult::totalPermittingRoles)
            .reduce(0, Integer::sum);
    }

    /**
     * Returns the representation of the evaluation result.
     * @return the representation of the evaluation result.
     */
    public String represent() {
        return evaluationRepresentation.represent();
    }

}
