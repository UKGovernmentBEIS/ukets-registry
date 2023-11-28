package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class AllocationPhaseTest {

    @Test
    public void testEquals() {
        AllocationPhase allocationPhase_1 = new AllocationPhase();
        allocationPhase_1.setCode(88);
        
        AllocationPhase allocationPhase_2 = new AllocationPhase();
        allocationPhase_2.setId(1L);
        allocationPhase_2.setCode(88);
        allocationPhase_2.setConsumedPhaseCap(120L);
        allocationPhase_2.setInitialPhaseCap(789L);
        allocationPhase_2.setPendingPhaseCap(8889L);
        
        AllocationPhase allocationPhase_3 = new AllocationPhase();
        allocationPhase_3.setCode(99);
        
        assertEquals(allocationPhase_1,allocationPhase_2);
        
        assertNotEquals(allocationPhase_1,allocationPhase_3);
        assertNotEquals(allocationPhase_2,allocationPhase_3);
        assertNotEquals(allocationPhase_1,new AllocationPhase());
        assertNotEquals(null,allocationPhase_1);
    }
}
