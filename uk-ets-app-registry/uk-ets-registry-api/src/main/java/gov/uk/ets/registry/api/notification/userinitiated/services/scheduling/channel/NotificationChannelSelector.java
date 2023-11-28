package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.channel;

import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.ChannelType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class NotificationChannelSelector {

    private final NotificationRepository notificationRepository;
    private final Set<NotificationChannelClient> clients;

    private EnumMap<ChannelType, NotificationChannelClient> channelClientsMap =
        new EnumMap<ChannelType, NotificationChannelClient>(ChannelType.class);

    public NotificationChannelSelector(NotificationRepository notificationRepository,
                                       Set<NotificationChannelClient> clients) {
        this.notificationRepository = notificationRepository;
        this.clients = clients;
        this.registerChannelClients();
    }

    public void forwardNotifications(List<IdentifiableEmailNotification> notifications) {
        notifications.forEach(
            instance -> getClient(instance).ifPresent(client -> client.dispatchNotification(instance))
        );
    }

    private Optional<NotificationChannelClient> getClient(
        IdentifiableEmailNotification identifiableEmailNotification) {
        Notification notification =
            notificationRepository.getByIdWithDefinition(identifiableEmailNotification.getNotificationId())
                .orElseThrow(() -> new IllegalStateException(
                    String.format("No notification with id %s", identifiableEmailNotification.getNotificationId())
                ));
        return Optional.ofNullable(channelClientsMap.get(notification.getDefinition().getChannel().getChannelType()));
    }

    private void registerChannelClients() {
        clients.forEach(client -> {
            ChannelType channelType = client.appliesFor();
            if (channelClientsMap.containsKey(channelType)) {
                throw new UkEtsException(String.format("Channel client already registered for:%s", channelType));
            } else {
                channelClientsMap.put(channelType, client);
            }
        });
    }

}
