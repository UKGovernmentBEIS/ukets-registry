package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * {@link GroupNotification} for confirming the email change.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Setter
public class EmailChangeConfirmationNotification extends EmailNotification {

    /**
     * The new user email
     */
    private String newUserEmail;
    /**
     * The confirmation url
     */
    private String confirmationUrl;
    /**
     * The expiration time in minutes
     */
    private Long expiration;

    @Builder
    public EmailChangeConfirmationNotification(String newUserEmail, String confirmationUrl, Long expiration) {
        super(Set.of(newUserEmail), GroupNotificationType.EMAIL_CHANGE_CONFIRMATION, null, null, null);
        this.newUserEmail = newUserEmail;
        this.confirmationUrl = confirmationUrl;
        this.expiration = expiration;

    }

}
