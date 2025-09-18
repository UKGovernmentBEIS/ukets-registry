package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;

import java.util.List;

public interface NotificationParameterRetriever {
    List<NotificationParameterHolder> getNotificationParameters(Notification notification);

    default NotificationParameterHolder getDefaultParameterHolder(Notification notification) {
        return NotificationParameterHolder.getDefaultParameterHolder(notification.getDefinition().getType());
    }
}
