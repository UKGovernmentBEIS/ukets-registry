package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.AbstractNotificationInstanceService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationUpdater;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.ScheduledNotificationsRetriever;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdHocEmailNotificationInstanceService extends AbstractNotificationInstanceService {

    public AdHocEmailNotificationInstanceService(NotificationSchedulingRepository schedulingRepository,
                                                 ScheduledNotificationsRetriever notificationsRetriever,
                                                 NotificationUpdater updater,
                                                 AdHocEmailNotificationParameterRetriever parameterRetriever,
                                                 AdHocEmailNotificationGenerator generator) {
        super(schedulingRepository, notificationsRetriever, updater, parameterRetriever, generator);
    }

    @Override
    protected String getSubject(Notification notification) {
        return notification.getShortText();
    }

    @Override
    protected String getBody(Notification notification) {
        return notification.getLongText();
    }

    @Override
    public List<NotificationType> getSupportedNotificationTypes() {
        return List.of(NotificationType.AD_HOC_EMAIL);
    }
}
