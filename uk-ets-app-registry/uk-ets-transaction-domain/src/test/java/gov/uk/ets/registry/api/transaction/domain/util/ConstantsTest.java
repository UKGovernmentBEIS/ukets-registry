package gov.uk.ets.registry.api.transaction.domain.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void getRegistryCode() {
        assertEquals("GB", Constants.getRegistryCode(true));
        assertEquals("UK", Constants.getRegistryCode(false));
    }
}