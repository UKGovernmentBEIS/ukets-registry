package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

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
public class UserInactivityNotificationDomainEventGenerator {
    private final EventService eventService;

    void generateUserEvent(NotificationParameterHolder ph, IdentifiableEmailNotification notification) {
        BaseNotificationParameters baseParameters = ph.getBaseNotificationParameters();
        String eventDescription = generateEventDescription(
            baseParameters.getUrid(),
            ph.getRecurrenceId(),
            notification
        );

        eventService.createAndPublishEvent(
            baseParameters.getUrid(),
            null, // = system event
            eventDescription,
            EventType.USER_INACTIVITY_NOTIFICATION_SENT,
            "User inactivity notification");
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
