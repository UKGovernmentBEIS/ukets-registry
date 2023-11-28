package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsUpdateNotification extends EmailNotification {
    private String emailAddress;
    private String requestId;
    private String userId;
    private TaskOutcome taskOutcome;

    @Builder
    public UserDetailsUpdateNotification(GroupNotificationType type, String emailAddress,
                                         String requestId, String userId, TaskOutcome taskOutcome) {
        super(Set.of(emailAddress), type, null, null, null);
        this.emailAddress = emailAddress;
        this.requestId = requestId;
        this.userId = userId;
        this. taskOutcome = taskOutcome;
    }

}
