package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountRelatedGroupNotification extends EmailNotification {
    private String requestId;
    private String accountFullIdentifier;
    private String accountIdentifier;
    private TaskOutcome taskOutcome;
    private String rejectionComment;
    private RequestType requestType;

    @Builder
    public AccountRelatedGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId,
                                           String accountFullIdentifier, String accountIdentifier,
                                           TaskOutcome taskOutcome, String rejectionComment, RequestType requestType) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.accountFullIdentifier = accountFullIdentifier;
        this.accountIdentifier = accountIdentifier;
        this.taskOutcome = taskOutcome;
        this.rejectionComment = rejectionComment;
        this.requestType = requestType;
    }
}
