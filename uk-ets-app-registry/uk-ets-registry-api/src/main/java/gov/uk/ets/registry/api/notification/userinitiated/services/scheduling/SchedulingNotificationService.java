package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.channel.NotificationChannelSelector;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SchedulingNotificationService {

    private final List<NotificationInstanceService> notificationInstanceServices;

    private final NotificationChannelSelector channelSelector;

    /**
     * Delegates to notification type specific services that assemble the notification instances to be sent.
     * Then delegates to the channel selector that will forward the notifications appropriately.
     */
    public void scheduleNotifications() {
        try {
            List<IdentifiableEmailNotification> notifications = notificationInstanceServices.stream()
                .map(NotificationInstanceService::generateNotificationInstances)
                .flatMap(Collection::stream)
                .collect(toList());

            log.info(
                "The following notifications will be dispatched: {}",
                notifications.stream()
                    .map(IdentifiableEmailNotification::getNotificationId)
                    .map(Objects::toString)
                    .collect(joining(","))
            );

            channelSelector.forwardNotifications(notifications);
        } catch (Exception e) {
            log.error("A scheduling notification error has occurred", e);
        }
    }
}
