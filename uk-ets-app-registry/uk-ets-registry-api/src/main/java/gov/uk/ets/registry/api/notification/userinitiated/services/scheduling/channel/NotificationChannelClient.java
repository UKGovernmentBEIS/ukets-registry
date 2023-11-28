package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.channel;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.ChannelType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;

public interface NotificationChannelClient<T extends IdentifiableEmailNotification> {

    ChannelType appliesFor();

    void dispatchNotification(T notification);
}
