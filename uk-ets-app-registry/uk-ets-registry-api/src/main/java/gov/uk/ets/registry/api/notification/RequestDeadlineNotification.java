package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
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
public class RequestDeadlineNotification extends EmailNotification {

    private String requestId;
    private Date deadline;

    @Builder
    public RequestDeadlineNotification(Set<String> recipients, GroupNotificationType type,  String requestId, Date deadline) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.deadline = deadline;
    }
}
