package uk.gov.ets.registration.user.common;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.HashSet;
import java.util.Set;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GeneratorServiceTest {

    @Autowired
    private GeneratorService generatorService;

    @Test
    void serviceIsAvailable() {
        assertNotNull(generatorService);
    }

    @Test
    void generateURID() {
        assertNotNull(generatorService.generateURID());
    }

    @Test
    void checkURIDRandomness() {
        Set<String> set = new HashSet<>();
        for (int index = 0; index < 10; index++) {
            set.add(generatorService.generateURID());
        }
        assertEquals(10, set.size());
    }

    @Test
    void validateURID() {
        for (int index = 0; index < 10; index++) {
            String urid = generatorService.generateURID();
            assertTrue(generatorService.validateURID(urid));
        }
    }

    @Test
    void invalidCheckDigits() {
        String urid = "UK132584430680";
        assertTrue(generatorService.validateURID(urid));

        urid = "UK132584430681";
        assertFalse(generatorService.validateURID(urid));
    }

}
