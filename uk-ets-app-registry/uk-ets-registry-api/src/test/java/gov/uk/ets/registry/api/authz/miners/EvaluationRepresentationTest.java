package gov.uk.ets.registry.api.authz.miners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EvaluationRepresentationTest {

    @Test
    public void testThatACorrectRepresentationIsReturned() {
        EvaluationResult result = new EvaluationResult("UNANIMOUS", "POSITIVE",
            new HashSet<>(MiningHelper.minedRole()), List.of());
        EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(result);

        assertEquals("role-1", evaluationRepresentation.represent().trim());
    }

    @Test
    public void testThatACorrectRepresentationIsReturnedWhenNegativeLogic() {
        EvaluationResult result = new EvaluationResult("UNANIMOUS", "NEGATIVE",
            new HashSet<>(MiningHelper.minedRole()), List.of());
        EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(result);

        assertEquals("not role-1", evaluationRepresentation.represent());
    }

    @Test
    public void testThatMatchingRolesAreProperlyCombinedWithUnanimous() {
        EvaluationResult result = new EvaluationResult("UNANIMOUS", "POSITIVE",
            new HashSet<>(MiningHelper.minedRoles()), List.of());
        EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(result);

        assertTrue(evaluationRepresentation.represent().contains("role-1"));
        assertTrue(evaluationRepresentation.represent().contains("role-2"));
        assertTrue(evaluationRepresentation.represent().contains("role-3"));
        assertTrue(evaluationRepresentation.represent().contains("and"));
        assertFalse(evaluationRepresentation.represent().contains("or"));
    }

    @Test
    public void testThatMatchingRolesAreProperlyCombinedWithAffirmativeAndNot() {
        EvaluationResult result = new EvaluationResult("AFFIRMATIVE", "NEGATIVE",
            new HashSet<>(MiningHelper.minedRoles()), List.of());
        EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(result);

        String represent = evaluationRepresentation.represent();
        assertTrue(represent.contains("role-1"));
        assertTrue(represent.contains("role-2"));
        assertTrue(represent.contains("role-3"));
        assertTrue(represent.contains("or"));
        assertTrue(represent.trim().startsWith("not"));
    }

    @Test
    public void testNestedEvaluationResults() {
        EvaluationResult result = new EvaluationResult("AFFIRMATIVE", "POSITIVE",
            new HashSet<>(List.of()), List.of(
            new EvaluationResult("AFFIRMATIVE", "NEGATIVE",
                new HashSet<>(MiningHelper.minedRoles()), List.of()
            ))
        );
        EvaluationRepresentation evaluationRepresentation = new EvaluationRepresentation(result);
        String represent = evaluationRepresentation.represent();
        assertTrue(represent.contains("role-1"));
        assertTrue(represent.contains("role-2"));
        assertTrue(represent.contains("role-3"));
        assertTrue(represent.contains("or"));
        assertFalse(represent.contains("and"));
        assertTrue(represent.trim().startsWith("[not"));
    }
}
