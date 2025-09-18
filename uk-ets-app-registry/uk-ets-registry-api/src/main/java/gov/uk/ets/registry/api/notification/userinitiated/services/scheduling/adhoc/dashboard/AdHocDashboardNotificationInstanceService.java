package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.dashboard;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
public class AdHocDashboardNotificationInstanceService {

    private final NotificationSchedulingRepository schedulingRepository;

    /**
     * Notification scheduling in the case of ad-hoc notifications means only to update the notification status.
     * It does not generate any notifications.
     **/
    public void updateNotificationStatus() {
        schedulingRepository.changeNotificationsStatus(LocalDateTime.now(UTC), List.of(NotificationType.AD_HOC.name()));
    }
}