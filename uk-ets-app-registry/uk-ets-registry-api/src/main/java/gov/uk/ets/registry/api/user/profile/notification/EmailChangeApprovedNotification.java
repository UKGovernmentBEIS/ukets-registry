package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * {@link GroupNotification} for notifying the user that the email change has been approved.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
public class EmailChangeApprovedNotification extends EmailNotification {

    /**
     * The new email of the user
     */
    private String newUserEmail;

    public EmailChangeApprovedNotification(String newUserEmail) {
        super(Set.of(newUserEmail), GroupNotificationType.EMAIL_CHANGE_APPROVED, null, null, null);
        this.newUserEmail = newUserEmail;
    }
}
