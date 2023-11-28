package gov.uk.ets.registry.api.user.profile.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * The information of the email change that initiated by user request.
 */
@Getter
@Builder
public class EmailChange {

    /**
     * The current / old email of user.
     */
    private String oldEmail;
    /**
     * The new email that replaces the current email of user.
     */
    private String newEmail;
    /**
    * The confirmation url sent to user new email
     */
    private String confirmationUrl;
    /**
     * The expiration time in minutes of the email change request.
     */
    private Long expiration;
}
