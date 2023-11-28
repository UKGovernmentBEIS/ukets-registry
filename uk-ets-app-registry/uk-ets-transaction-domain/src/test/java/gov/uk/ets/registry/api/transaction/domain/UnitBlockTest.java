package gov.uk.ets.registry.api.transaction.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class UnitBlockTest {
    @Test
    public void testEquals() {
        UnitBlock unitBlock_1 = new UnitBlock();
        unitBlock_1.setStartBlock(1200L);
        unitBlock_1.setEndBlock(1250L);
        unitBlock_1.setOriginatingCountryCode("GR");
        
        UnitBlock unitBlock_2 = new UnitBlock();
        unitBlock_2.setStartBlock(1200L);
        unitBlock_2.setEndBlock(1250L);
        unitBlock_2.setOriginatingCountryCode("GR");
        
        UnitBlock unitBlock_3 = new UnitBlock();
        unitBlock_3.setStartBlock(400L);
        unitBlock_3.setEndBlock(500L);
        unitBlock_3.setOriginatingCountryCode("GR");
        
        assertEquals(unitBlock_1,unitBlock_2);
        
        assertNotEquals(unitBlock_1,unitBlock_3);
        assertNotEquals(unitBlock_2,unitBlock_3);
    }
}
