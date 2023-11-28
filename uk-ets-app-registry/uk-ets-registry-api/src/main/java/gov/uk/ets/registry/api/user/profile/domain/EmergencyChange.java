package gov.uk.ets.registry.api.user.profile.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * The information of the emergency change that initiated by user request.
 */
@Getter
@Builder
public class EmergencyChange {

    /**
     * The current email of user.
     */
    private final String email;

    /**
     * The verification url sent to user new email
     */
    private final String verificationUrl;
    /**
     * The expiration time in minutes of the email change request.
     */
    private final Long expiration;
}
