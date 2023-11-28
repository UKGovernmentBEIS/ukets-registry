package gov.uk.ets.registry.api.user;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;


/**
 * Tests random artifacts related to users (e.g. URID, enrolment key).
 */
class UserGeneratorServiceTests {

    private final UserGeneratorService userGeneratorService = new UserGeneratorService();

    @Test
    void testValidURIDs() throws NoSuchAlgorithmException {
        for (int index = 0; index < 10; index++) {
            final String urid = userGeneratorService.generateURID();
            assertTrue(userGeneratorService.validateURID(urid));
            System.out.println(urid);
        }
    }

    @Test
    void testValidEnrolmentKeys() throws NoSuchAlgorithmException {
        for (int index = 0; index < 10; index++) {
            final String key = userGeneratorService.generateEnrolmentKey();
            assertTrue(userGeneratorService.validateEnrolmentKey(key));
            System.out.println(key);
        }
    }

    @Test
    void enrolmentKeyHasWrongLength() {
        assertTrue(userGeneratorService.validateEnrolmentKey("9GDT-62T8-3HEE-YWLW-YTH2"));
        assertFalse(userGeneratorService.validateEnrolmentKey("9GDT-62T8-3HEE-YWLW-YTH22"));
        assertFalse(userGeneratorService.validateEnrolmentKey("9GDT-62T8-3HEE-YWLW-YTH"));
    }

    @Test
    void enrolmentKeyHasWrongNumberOfDashes() {
        assertTrue(userGeneratorService.validateEnrolmentKey("B3PC-VTMP-QJCB-7U7D-LAXC"));
        assertFalse(userGeneratorService.validateEnrolmentKey("B3PC-VTMP-QJCBA7U7D-LAXC"));
        assertFalse(userGeneratorService.validateEnrolmentKey("B3PC-VTMP-QJCBA7U7D-LA-C"));
    }

    @Test
    void enrolmentKeyContainsIllegalCharacter() {
        assertTrue(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-79HY-KZM3-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W0TT-JNBA-79HY-KZM3-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-79HY-KOM3-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-19HY-KZM3-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-79IY-KZM3-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-79HY-KZM5-JMK7"));
        assertFalse(userGeneratorService.validateEnrolmentKey("W9TT-JNBA-79HY-KZM3-JMS7"));
    }

    @Test
    void enrolmentKeyHasDashesInWrongPosition() {
        assertTrue(userGeneratorService.validateEnrolmentKey("4A3R-3A2U-KGDJ-QVQR-Q9N9"));
        assertFalse(userGeneratorService.validateEnrolmentKey("4A3-R3A2U-KGDJ-QVQR-Q9N9"));
        assertFalse(userGeneratorService.validateEnrolmentKey("4A3R-3A2U-KGDJQVQR-Q-9N9"));
    }

    @Test
    void enrolmentKeyContainsLowerCaseCharacter() {
        assertTrue(userGeneratorService.validateEnrolmentKey("QHTF-AT8X-RN6R-FDFY-AZAC"));
        assertFalse(userGeneratorService.validateEnrolmentKey("QHTF-AT8X-Rn6R-FDFY-AZAC"));
    }

    @Test
    void enrolmentKeyContainsSymbol() {
        assertTrue(userGeneratorService.validateEnrolmentKey("QHTF-AT8X-RN6R-FDFY-AZAC"));
        assertFalse(userGeneratorService.validateEnrolmentKey("QHTF-@T8X-RN6R-FDFY-AZAC"));
    }

}
