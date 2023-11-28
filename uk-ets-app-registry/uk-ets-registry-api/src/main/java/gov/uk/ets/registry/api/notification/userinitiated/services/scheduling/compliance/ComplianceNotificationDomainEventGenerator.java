package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates domain events about notifications.
 */
@Component
@RequiredArgsConstructor
public class ComplianceNotificationDomainEventGenerator {
    private final EventService eventService;

    void generateAccountEvent(NotificationParameterHolder ph, IdentifiableEmailNotification notification) {
        BaseNotificationParameters baseParameters = ph.getBaseNotificationParameters();
        String eventDescription = generateEventDescription(
            baseParameters.getDisclosedName(),
            ph.getRecurrenceId(),
            notification
        );

        eventService.createAndPublishEvent(
            baseParameters.getAccountIdentifier().toString(),
            null, // = system event
            eventDescription,
            EventType.NOTIFICATION_SENT,
            "Notification sent");
    }

    private String generateEventDescription(String disclosedName, Long recurrenceId,
                                            IdentifiableEmailNotification notification) {
        return String.format(
            "Subject: %s \n Notification ID: %s \n Recurrence ID: %s \n AR: %s",
            notification.getSubject(),
            notification.getNotificationId(),
            recurrenceId,
            disclosedName
        );
    }
}
