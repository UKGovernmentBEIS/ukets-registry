package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationSearchCriteria {

    private NotificationType type;
}
