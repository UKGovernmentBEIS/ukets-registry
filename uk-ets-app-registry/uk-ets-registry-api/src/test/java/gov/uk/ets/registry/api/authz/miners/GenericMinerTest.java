package gov.uk.ets.registry.api.authz.miners;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GenericMinerTest {

    @Test
    public void testThatNonEmptyJSONArraysAreMinedProperly() {
        TestGenericMiner miner = new TestGenericMiner();
        List<String> minedItems = miner.mine(configuration(), "mine-this");
        assertTrue(minedItems.contains("Any Non Registry Administrator Policy"));
        assertTrue(minedItems.contains("Enrolled User Policy"));
    }

    @Test
    public void testThatEmptyJSONArraysAreMinedProperly() {
        TestGenericMiner miner = new TestGenericMiner();
        List<String> minedItems = miner.mine(configuration(), "mine-empty");
        assertTrue(minedItems.isEmpty());
    }

    private Map<String, String> configuration() {
        return Map.of("mine-this", "[\"Any Non Registry Administrator Policy\",\"Enrolled User Policy\"]",
            "mine-empty", "");
    }

    private static class TestGenericMiner implements GenericMiner {

    }
}
