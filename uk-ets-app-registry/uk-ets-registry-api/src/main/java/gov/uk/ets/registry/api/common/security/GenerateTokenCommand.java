package gov.uk.ets.registry.api.common.security;

import lombok.Builder;
import lombok.Getter;

/**
 * Command to generate AWT token
 */
@Builder
@Getter
public class GenerateTokenCommand {
    /**
     * The payload that token should carry.
     */
    private String payload;
    /**
     * The expiration time in minutes.
     */
    private Long expiration;
}
