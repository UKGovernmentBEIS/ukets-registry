package gov.uk.ets.registry.api.allocation.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocationTypeTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    public void test() {
        assertNull(AllocationType.parse(""));
        assertNull(AllocationType.parse(null));
        assertNull(AllocationType.parse("CCC"));
        assertEquals(AllocationType.NAT, AllocationType.parse("NAT"));
        assertEquals(AllocationType.NER, AllocationType.parse("NER"));
        assertEquals(AllocationType.NAVAT, AllocationType.parse("NAVAT"));

        assertTrue(AllocationType.NAT.isRelatedWithInstallation());
        assertFalse(AllocationType.NAVAT.isRelatedWithInstallation());
        assertTrue(AllocationType.NER.isRelatedWithInstallation());

        assertFalse(AllocationType.NAT.isRelatedWithAircraftOperator());
        assertTrue(AllocationType.NAVAT.isRelatedWithAircraftOperator());
        assertFalse(AllocationType.NER.isRelatedWithAircraftOperator());
    }
}