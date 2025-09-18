package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@RequiredArgsConstructor
public abstract class AbstractNotificationInstanceService {

    protected final NotificationSchedulingRepository schedulingRepository;
    protected final ScheduledNotificationsRetriever notificationsRetriever;
    protected final NotificationUpdater updater;
    protected final NotificationParameterRetriever parameterRetriever;
    protected final NotificationGenerator generator;

    public final List<IdentifiableEmailNotification> generateNotificationInstances() {

        List<NotificationType> types = getSupportedNotificationTypes();

        // Update statuses for the given notification types
        schedulingRepository.changeNotificationsStatus(LocalDateTime.now(UTC),
            types.stream().map(Enum::name).collect(Collectors.toList()));

        // Retrieve notifications that are ready to be sent
        List<Notification> notifications = notificationsRetriever.getNotificationsToBeSent(types);

        // Update each notification status
        notifications.forEach(updater::update);

        // Generate email notifications
        return notifications.stream()
            .flatMap(notification -> parameterRetriever.getNotificationParameters(notification).stream()
                .map(holder -> {
                    // Use abstract methods for text retrieval
                    String shortText = getSubject(notification);
                    String longText = getBody(notification);

                    IdentifiableEmailNotification emailNotification = generator.generate(holder, shortText, longText);

                    // Optional event generation (child class can override this)
                    handleEventGeneration(holder, emailNotification);

                    return emailNotification;
                })
            ).collect(Collectors.toList());
    }

    protected abstract String getSubject(Notification notification);

    protected abstract String getBody(Notification notification);

    /**
     * Method for optional event generation.
     * Default implementation does nothing. Child classes can override if needed.
     */
    protected void handleEventGeneration(NotificationParameterHolder holder, IdentifiableEmailNotification emailNotification) {
        // Default: Do nothing
    }

    public abstract List<NotificationType> getSupportedNotificationTypes();

    public final void validate(Notification notification) {
        NotificationParameterHolder parameterHolder = parameterRetriever.getDefaultParameterHolder(notification);
        try {
            generator.generate(parameterHolder, "", notification.getLongText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing template. Invalid parameter name.", e);
        }
    }
}

