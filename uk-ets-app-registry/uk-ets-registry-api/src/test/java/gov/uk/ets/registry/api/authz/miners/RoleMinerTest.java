package gov.uk.ets.registry.api.authz.miners;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class RoleMinerTest {

    @Test
    public void testThatNonEmptyJSONArraysAreMinedProperly() {
        TestRoleMiner miner = new TestRoleMiner();
        List<String> minedItems = miner.mineRole(configuration());
        assertTrue(minedItems.contains("uk-ets-registry-api/readonly-administrator"));
        assertFalse(minedItems.contains("uk-ets-registry-api/another-administrator"));
        assertFalse(minedItems.contains("id"));
        assertFalse(minedItems.contains("required"));
        assertFalse(minedItems.contains("true"));
    }

    @Test
    public void testThatEmptyJSONArraysAreMinedProperly() {
        TestRoleMiner miner = new TestRoleMiner();
        List<String> minedItems = miner.mineRole(configuration(""));
        assertTrue(minedItems.isEmpty());
    }

    @Test
    public void testThatEmptyJSONArraysAreMinedProperly2() {
        TestRoleMiner miner = new TestRoleMiner();
        List<String> minedItems = miner.mineRole(configuration("[]"));
        assertTrue(minedItems.isEmpty());
    }

    private static class TestRoleMiner implements RoleMiner {
    }

    private Map<String, String> configuration() {
        return Map.of("roles", "[{\"id\":\"uk-ets-registry-api/readonly-administrator\",\"required\":true}]",
            "roles-empty", "[{\"id\":\"uk-ets-registry-api/another-administrator\",\"required\":true}]");
    }

    private Map<String, String> configuration(String jsonArray) {
        return Map.of("roles", jsonArray);
    }

}
