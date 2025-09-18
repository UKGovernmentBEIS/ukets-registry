package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;

public interface NotificationGenerator {
    IdentifiableEmailNotification generate(NotificationParameterHolder recipient, String emailSubject, String emailBody);
}
