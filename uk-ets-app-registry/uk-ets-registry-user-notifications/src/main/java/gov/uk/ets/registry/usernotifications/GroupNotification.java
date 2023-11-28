package gov.uk.ets.registry.usernotifications;

import java.io.Serializable;
import java.util.Set;

/**
 * The basic contract of a notification that is sent to many users.
 */
public interface GroupNotification extends Serializable, MultipartEmailWithSubject {
    /**
     * The recipients of the notification (email address).
     * @return the emails of the recipients
     */
    Set<String> recipients();

    /**
     * The type of the notification.
     * @return the notification type.
     */
    GroupNotificationType type();
}
