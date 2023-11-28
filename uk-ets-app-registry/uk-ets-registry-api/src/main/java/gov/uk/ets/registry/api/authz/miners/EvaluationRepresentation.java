package gov.uk.ets.registry.api.authz.miners;

import org.springframework.util.StringUtils;

/**
 * Responsible for representing the evaluation result.
 */
public class EvaluationRepresentation {
    private final EvaluationResult evaluationResult;

    public EvaluationRepresentation(EvaluationResult evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    /**
     * Returns the representation of the evaluation result.
     *
     * @return the representation of the evaluation result.
     */
    public String represent() {
        String representation = "";
        if (!evaluationResult.getRoles().isEmpty()) {
            String rolesRepresentation = evaluationResult.getRoles().stream()
                .map(MinedRole::getId)
                .reduce("", (r1, r2) -> evaluationResult.evaluationRepresentation
                    .combine(r1, strategyOperator(evaluationResult), r2));
            if (evaluationResult.getRoles().size() > 1) {
                representation = logicOperator(evaluationResult) + " (" + rolesRepresentation + ")";
            } else if (evaluationResult.getRoles().size() == 1) {
                representation = logicOperator(evaluationResult) + " " + rolesRepresentation;
            }
        }
        String childRoleRepresentation = evaluationResult.getChildEvaluationResults()
            .stream()
            .map(EvaluationResult::represent)
            .reduce("", (r1, r2) -> evaluationResult.evaluationRepresentation
                .combine(r1, strategyOperator(evaluationResult), r2));
        if (!StringUtils.isEmpty(childRoleRepresentation)) {
            if (childRoleRepresentation.startsWith("[") && childRoleRepresentation.endsWith("]")) { // dont redo the [ ]
                representation += logicOperator(evaluationResult) + childRoleRepresentation;
            } else {
                representation += logicOperator(evaluationResult) + "[" + childRoleRepresentation + "]";
            }
        }
        return representation;
    }

    /**
     * Combine two terms with an operator.
     * @param term1 term a
     * @param op operator
     * @param term2 term b
     * @return the combination
     */
    private String combine(String term1, String op, String term2) {
        if (StringUtils.isEmpty(term1) && StringUtils.isEmpty(term2)) {
            return "";
        }
        if (StringUtils.isEmpty(term1)) {
            return term2;
        }
        if (StringUtils.isEmpty(term2)) {
            return term1;
        }
        return term1 + " " + op + " " + term2;
    }

    private String strategyOperator(EvaluationResult evaluationResult) {
        if ("UNANIMOUS".equals(evaluationResult.decisionStrategy)) {
            return " and ";
        } else if ("AFFIRMATIVE".equals(evaluationResult.decisionStrategy)) {
            return " or ";
        }
        return " and ";
    }

    private String logicOperator(EvaluationResult evaluationResult) {
        if ("POSITIVE".equals(evaluationResult.logic)) {
            return "";
        } else if ("NEGATIVE".equals(evaluationResult.logic)) {
            return "not";
        }
        return "";
    }
}