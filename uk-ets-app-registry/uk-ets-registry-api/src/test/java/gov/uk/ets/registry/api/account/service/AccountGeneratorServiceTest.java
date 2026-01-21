package gov.uk.ets.registry.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AccountGeneratorServiceTest {

    @InjectMocks
    private AccountGeneratorService accountGeneratorService;


    @Test
    void testGenerateAccountClaimCode() throws NoSuchAlgorithmException {

        String code = accountGeneratorService.generateAccountClaimCode();

        // Validate correct format
        assertNotNull(code);
        assertEquals(12, code.length());
        assertTrue(code.startsWith("ACC"));
    }
}
