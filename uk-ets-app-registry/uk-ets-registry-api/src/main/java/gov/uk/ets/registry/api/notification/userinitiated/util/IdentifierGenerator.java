package gov.uk.ets.registry.api.notification.userinitiated.util;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdentifierGenerator {

    /**
     * Assumes that the state of the notification is from the previous run (if available).
     * That is why it adds 1 to the timesFired field retrieved from the db.
     */
    public String generate(Notification notification) {
        return notification.getDefinition().getTypeId() + "-" +
            notification.getId() + "-" +
            (notification.getTimesFired() == null ? "1" : notification.getTimesFired() + 1);
    }
}
