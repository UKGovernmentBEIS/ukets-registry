package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import java.util.List;

public interface NotificationInstanceService {
    List<IdentifiableEmailNotification> generateNotificationInstances();
}
