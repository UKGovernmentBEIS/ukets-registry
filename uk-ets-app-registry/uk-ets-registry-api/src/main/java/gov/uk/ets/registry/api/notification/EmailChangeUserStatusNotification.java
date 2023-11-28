package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class EmailChangeUserStatusNotification extends EmailNotification {

    private String emailAddress;

    @Builder
    public EmailChangeUserStatusNotification(String emailAddress) {
        super(Set.of(emailAddress), GroupNotificationType.EMAIL_CHANGE_STATUS, null, null, null);
        this.emailAddress = emailAddress;
    }

}