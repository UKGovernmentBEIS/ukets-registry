package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Extends a {@link GroupNotification} with transaction related data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountOpeningGroupNotification extends EmailNotification {
    private String requestId;
    private String accountFullIdentifier;
    private TaskOutcome taskOutcome;
    private String rejectionComment;

    @Builder
    public AccountOpeningGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId,
                                           String accountFullIdentifier, TaskOutcome taskOutcome,
                                           String rejectionComment) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.accountFullIdentifier = accountFullIdentifier;
        this.taskOutcome = taskOutcome;
        this.rejectionComment = rejectionComment;
    }
}
