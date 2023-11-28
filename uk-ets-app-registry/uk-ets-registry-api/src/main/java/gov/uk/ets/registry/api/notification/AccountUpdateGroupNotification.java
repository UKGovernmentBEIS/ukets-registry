package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
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
public class AccountUpdateGroupNotification extends EmailNotification {
    private String requestId;
    private String accountIdentifier;
    private String accountFullIdentifier;
    private TaskOutcome taskOutcome;
    private RequestType requestType;

    @Builder
    public AccountUpdateGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId,
                                          String accountIdentifier, String accountFullIdentifier,
                                          TaskOutcome taskOutcome, RequestType requestType) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.accountIdentifier = accountIdentifier;
        this.accountFullIdentifier = accountFullIdentifier;
        this.taskOutcome = taskOutcome;
        this.requestType = requestType;
    }
}
