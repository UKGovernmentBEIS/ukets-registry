package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedGroupNotification extends EmailNotification {

    private Long requestId;

    @Builder
    public PaymentCompletedGroupNotification(Set<String> recipients, GroupNotificationType type, Long requestId) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
    }
}
