package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * {@link GroupNotification} for notifying the user that the email change has been rejected.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
public class EmailChangeRejectedNotification extends EmailNotification {
    /**
     * The current email of the user
     */
    private String currentUserEmail;

    /**
     * The rejection reason
     */
    private String rejectionReason;

    public EmailChangeRejectedNotification(String currentUserEmail, String rejectionReason){
        super(Set.of(currentUserEmail), GroupNotificationType.EMAIL_CHANGE_REJECTED, null, null, null);
        this.currentUserEmail = currentUserEmail;
        this.rejectionReason = rejectionReason;
    }
}
