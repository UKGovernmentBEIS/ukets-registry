package gov.uk.ets.registry.api.notification;

import java.util.Set;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordSuccessNotification extends EmailNotification {

    private String emailAddress;

    @Builder
    public ResetPasswordSuccessNotification(String emailAddress) {
        super(Set.of(emailAddress), GroupNotificationType.RESET_PASSWORD_SUCCESS, null, null, null);
        this.emailAddress = emailAddress;
    }

}
