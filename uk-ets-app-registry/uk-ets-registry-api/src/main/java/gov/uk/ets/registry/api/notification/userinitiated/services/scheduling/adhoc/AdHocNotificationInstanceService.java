package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc;

import static java.time.ZoneOffset.UTC;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationInstanceService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdHocNotificationInstanceService implements NotificationInstanceService {

    private final NotificationSchedulingRepository notificationSchedulingRepository;

    /**
     * Notification scheduling in the case of ad-hoc notifications means only to update the notification status.
     * It does not generate any notifications.
     **/
    @Override
    public List<IdentifiableEmailNotification> generateNotificationInstances() {
        notificationSchedulingRepository.changeAdHocNotificationsStatus(LocalDateTime.now(UTC));
        return List.of();
    }
}
