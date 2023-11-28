package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class UnitBlockTest {
    @Test
    public void testEquals() {
        UnitBlock unitBlock_1 = new UnitBlock();
        unitBlock_1.setStartBlock(1200L);
        unitBlock_1.setEndBlock(1250L);
        
        UnitBlock unitBlock_2 = new UnitBlock();
        unitBlock_2.setStartBlock(1200L);
        unitBlock_2.setEndBlock(1250L);
        
        UnitBlock unitBlock_3 = new UnitBlock();
        unitBlock_3.setStartBlock(400L);
        unitBlock_3.setEndBlock(500L);
        
        UnitBlock unitBlock_4 = new UnitBlock();
        unitBlock_4.setStartBlock(1200L);
        unitBlock_4.setEndBlock(null);
        
        UnitBlock unitBlock_5 = new UnitBlock();
        unitBlock_5.setStartBlock(null);
        unitBlock_5.setEndBlock(1250L);
        
        UnitBlock unitBlock_6 = new UnitBlock();
        unitBlock_6.setStartBlock(null);
        unitBlock_6.setEndBlock(null);
        
        assertEquals(unitBlock_1,unitBlock_2);
        
        assertNotEquals(unitBlock_1,unitBlock_3);
        assertNotEquals(unitBlock_2,unitBlock_3);
        assertNotEquals(unitBlock_1,unitBlock_4);
        assertNotEquals(unitBlock_1,unitBlock_5);
        assertNotEquals(unitBlock_1,unitBlock_6);
        assertNotEquals(null,unitBlock_1);
    }
}
