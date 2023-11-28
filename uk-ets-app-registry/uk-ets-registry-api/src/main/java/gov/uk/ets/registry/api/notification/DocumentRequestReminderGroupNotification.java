package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DocumentRequestReminderGroupNotification extends EmailNotification {

    private String requestId;

    @Builder
    public DocumentRequestReminderGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
    }
}
