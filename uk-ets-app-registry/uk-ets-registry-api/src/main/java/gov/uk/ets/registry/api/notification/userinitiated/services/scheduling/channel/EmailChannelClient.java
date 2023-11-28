package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.channel;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.ChannelType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailChannelClient implements NotificationChannelClient<IdentifiableEmailNotification> {

    private final GroupNotificationClient groupNotificationClient;

    @Override
    public ChannelType appliesFor() {
        return ChannelType.EMAIL;
    }

    @Override
    public void dispatchNotification(IdentifiableEmailNotification notification) {
        log.info("notification instance id is being dispatched: {}", notification.getNotificationInstanceId());
        groupNotificationClient.emitUserDefinedNotification(notification);
    }
}
