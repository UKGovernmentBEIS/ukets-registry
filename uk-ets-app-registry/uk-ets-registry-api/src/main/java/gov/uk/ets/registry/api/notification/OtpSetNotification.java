package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class OtpSetNotification extends EmailNotification {

    private String emailAddress;

    public OtpSetNotification(String emailAddress) {
        super(Set.of(emailAddress), GroupNotificationType.OTP_SET, null, null, null);
        this.emailAddress = emailAddress;
    }
}
