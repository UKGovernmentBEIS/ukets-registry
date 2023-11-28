package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountClosureGroupNotification extends EmailNotification {
    private String requestId;
    private String accountFullIdentifier;

    @Builder
    public AccountClosureGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId, String accountFullIdentifier){
        super(recipients, type, null, null, null);
        this.accountFullIdentifier = accountFullIdentifier;
        this.requestId = requestId;
    }
}
