package gov.uk.ets.registry.api.notification;

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
public class AccountProposalGroupNotification extends EmailNotification {
    private String requestId;

    @Builder
    public AccountProposalGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
    }
}
