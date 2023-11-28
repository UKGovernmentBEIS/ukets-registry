package gov.uk.ets.registry.api.notification.userinitiated.util;


import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Helper methods that find elements inside collections.
 */
@UtilityClass
public class NotificationFinders {

    public static NotificationDefinition findDefinition(List<Notification> notifications,
                                                        Long notificationId) {
        return notifications.stream()
            .filter(n -> n.getId().equals(notificationId))
            .map(Notification::getDefinition)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                String.format("Definition not found for notification id: %s", notificationId)
            ));
    }

    public static IdentifiableEmailNotification findNotification(List<IdentifiableEmailNotification> emailNotifications,
                                                                 NotificationParameterHolder ph) {
        return emailNotifications.stream()
            .filter(n -> n.getNotificationId().equals(ph.getNotificationId()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("Notification with id %s not found.", ph.getNotificationId())
                )
            );
    }
}
