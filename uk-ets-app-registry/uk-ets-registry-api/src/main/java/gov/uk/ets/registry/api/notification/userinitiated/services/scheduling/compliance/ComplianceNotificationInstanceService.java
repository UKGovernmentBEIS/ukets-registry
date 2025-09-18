package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.AbstractNotificationInstanceService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationUpdater;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.ScheduledNotificationsRetriever;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplianceNotificationInstanceService extends AbstractNotificationInstanceService {

    private final ComplianceNotificationDomainEventGenerator eventGenerator;

    public ComplianceNotificationInstanceService(NotificationSchedulingRepository schedulingRepository,
                                                 ScheduledNotificationsRetriever notificationsRetriever,
                                                 NotificationUpdater updater,
                                                 ComplianceNotificationParameterRetriever parameterRetriever,
                                                 ComplianceNotificationGenerator generator,
                                                 ComplianceNotificationDomainEventGenerator eventGenerator) {
        super(schedulingRepository, notificationsRetriever, updater, parameterRetriever, generator);
        this.eventGenerator = eventGenerator;
    }

    @Override
    protected String getSubject(Notification notification) {
        return notification.getDefinition().getShortText();
    }

    @Override
    protected String getBody(Notification notification) {
        return notification.getDefinition().getLongText();
    }

    @Override
    protected void handleEventGeneration(NotificationParameterHolder holder, IdentifiableEmailNotification emailNotification) {
        eventGenerator.generateAccountEvent(holder, emailNotification);
    }

    @Override
    public List<NotificationType> getSupportedNotificationTypes() {
        return NotificationType.getComplianceNotificationTypes();
    }
}
