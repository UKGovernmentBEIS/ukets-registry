package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static gov.uk.ets.registry.api.notification.userinitiated.util.NotificationFinders.findDefinition;
import static gov.uk.ets.registry.api.notification.userinitiated.util.NotificationFinders.findNotification;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationInstanceService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the generation of notifications.
 */
@Service
@RequiredArgsConstructor
public class ComplianceNotificationInstanceService implements NotificationInstanceService {

    private final NotificationSchedulingRepository schedulingRepository;
    private final ScheduledNotificationsRetriever notificationsRetriever;
    private final NotificationParameterRetriever parameterRetriever;
    private final ComplianceNotificationGenerator generator;
    private final ComplianceNotificationDomainEventGenerator eventGenerator;
    private final NotificationUpdater updater;

    @Transactional
    @Override
    public List<IdentifiableEmailNotification> generateNotificationInstances() {
        // update statuses
        schedulingRepository.changeComplianceNotificationsStatus(LocalDateTime.now(UTC));

        // filter notifications that must be sent according to schedule
        List<Notification> notifications = notificationsRetriever.getNotificationsToBeSent();

        // retrieve a flat list (for all notifications) of all parameters together with the recipient emails
        List<NotificationParameterHolder> parameterHolders =
            notifications.stream()
                .map(parameterRetriever::getNotificationParameters)
                .flatMap(Collection::stream)
                .collect(toList());

        // create email messages
        List<IdentifiableEmailNotification> emailNotifications = parameterHolders.stream()
            .map(holder -> generator.generate(holder, findDefinition(notifications, holder.getNotificationId())))
            .collect(toList());

        // update schedule information
        notifications.forEach(updater::update);

        // create domain events
        parameterHolders.forEach(
            ph -> eventGenerator.generateAccountEvent(ph, findNotification(emailNotifications, ph))
        );

        return emailNotifications;
    }
}
