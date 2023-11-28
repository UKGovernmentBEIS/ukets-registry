package gov.uk.ets.registry.api.common.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenVerifierTest {
    private TokenVerifier tokenVerifier;

    @BeforeEach
    void setup() {
        tokenVerifier = new TokenVerifier(
            "issuer",
            "clientId",
            "secret"
        );
    }

    @Test
    void testPayloadTransfer() {
        // given
        String payload = "{"
            + "\"name\":\"payload\","
            + "\"type\":\"TRANSFER\","
            + " \"count\":\"20L\""
            + "}";
        // when
        String token = tokenVerifier.generateToken(GenerateTokenCommand.builder()
            .expiration(60L)
            .payload(payload)
            .build());
        assertNotNull(token);

        String transferredPayload = tokenVerifier.getPayload(token);
        assertEquals(payload, transferredPayload);
    }
}