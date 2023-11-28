package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link GroupNotification} for notifying the user for an email change that requested.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class EmailChangeRequestedNotification extends EmailNotification {
    /**
     * The current/ old user email.
     */
    private String currentUserEmail;

    public EmailChangeRequestedNotification(String currentUserEmail){
        super(Set.of(currentUserEmail), GroupNotificationType.EMAIL_CHANGE_REQUESTED, null, null, null);
        this.currentUserEmail = currentUserEmail;
    }

}
