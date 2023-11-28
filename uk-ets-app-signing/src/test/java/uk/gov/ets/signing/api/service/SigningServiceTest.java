package uk.gov.ets.signing.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.Signature;
import org.springframework.vault.support.VaultSignRequest;
import uk.gov.ets.signing.api.SigningProperties;

@ExtendWith(MockitoExtension.class)
class SigningServiceTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VaultOperations vaultTemplate;

    private SigningService signingService;

    private SigningProperties signingProperties;
    public static final String DATA = "test data";

    @BeforeEach
    public void setup() {
        signingProperties = new SigningProperties();
        signingService = new SigningService(vaultTemplate, signingProperties);
    }

    @DisplayName("Test sign data")
    @Test
    public void testSign() {
        Signature expectedSignature = Signature.of("signature result");
        Mockito.when(vaultTemplate.opsForTransit(signingProperties.getVault().getEngine())
            .sign(eq(signingProperties.getVault().getKey()), any(VaultSignRequest.class)))
            .thenReturn(expectedSignature);
        Signature actualSignature = signingService.sign(DATA);
        assertEquals(expectedSignature, actualSignature);
    }

    @DisplayName("Test verify data success")
    @Test
    public void testVerifySuccess() {
        String signature= "my signature";

        Mockito.when(vaultTemplate.opsForTransit(signingProperties.getVault().getEngine())
            .verify(eq(signingProperties.getVault().getKey()), any(Plaintext.class), any(Signature.class)))
            .thenReturn(true);
        boolean verify = signingService.verify(signature, DATA);
        assertEquals(true, verify);
    }

    @DisplayName("Test verify data failure")
    @Test
    public void testVerifyFailure() {
        String signature= "my signature";
        Mockito.when(vaultTemplate.opsForTransit(signingProperties.getVault().getEngine())
            .verify(eq(signingProperties.getVault().getKey()), any(Plaintext.class), any(Signature.class)))
            .thenReturn(false);
        boolean verify = signingService.verify(signature, DATA);
        assertEquals(false, verify);
    }
}
