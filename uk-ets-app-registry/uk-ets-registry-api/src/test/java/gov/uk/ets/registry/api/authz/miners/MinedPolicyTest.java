package gov.uk.ets.registry.api.authz.miners;

import static gov.uk.ets.registry.api.authz.miners.MiningHelper.minedResource;
import static gov.uk.ets.registry.api.authz.miners.MiningHelper.minedRole;
import static gov.uk.ets.registry.api.authz.miners.MiningHelper.minedScope;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MinedPolicyTest {

    @Test
    public void testThatAPolicyReturnsTheRoleThatHasAccessToAScope() {
        MinedPolicy minedPolicy = new MinedPolicy("Role 1", "POSITIVE", "UNANIMOUS",
            "role", new ArrayList<>(), minedRole(), minedScope(), List.of());

        MinedPolicy minedPermission = new MinedPolicy("Scope Permission 1", "POSITIVE",
            "UNANIMOUS", "scope", List.of(), List.of(), minedScope(), List.of());

        EvaluationResult evaluationResult = minedPolicy.evaluationResultFromScope(minedPermission, new MinedScope("scope-1"));
        assertEquals("role-1", evaluationResult.represent().trim());
    }

    @Test
    public void testThatAPolicyReturnsTheRoleThatHasAccessToAResource() {
        MinedPolicy minedPolicy = new MinedPolicy("Role 1", "POSITIVE", "UNANIMOUS",
            "role", new ArrayList<>(), minedRole(), minedScope(), List.of());

        MinedPolicy minedPermission = new MinedPolicy("Resource Permission 1", "POSITIVE",
            "UNANIMOUS", "resource", List.of(), List.of(), List.of(), minedResource());

        EvaluationResult evaluationResult = minedPolicy.evaluationResultFromResource(minedPermission,
            new MinedResource("resource-1", Set.of(), List.of()));
        assertEquals("role-1", evaluationResult.represent().trim());
    }


}
