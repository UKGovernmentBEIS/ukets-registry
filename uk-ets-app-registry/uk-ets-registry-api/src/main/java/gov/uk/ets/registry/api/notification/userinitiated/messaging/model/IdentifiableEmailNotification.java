package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.channel.NotificationChannelSelector;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * This is an email notification which contains an id.
 * This is needed in the {@link NotificationChannelSelector} which needs to retrieve the notification definition's
 * channel.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IdentifiableEmailNotification extends EmailNotification {
    private Long notificationId;
    private String notificationInstanceId;

    @Builder
    public IdentifiableEmailNotification(Set<String> recipients,
                                         GroupNotificationType type, String subject,
                                         String bodyHtml, String bodyPlain, Long notificationId,
                                         String notificationInstanceId, boolean includeBcc) {
        super(recipients, type, subject, bodyHtml, bodyPlain, includeBcc);
        this.notificationId = notificationId;
        this.notificationInstanceId = notificationInstanceId;
    }
}
