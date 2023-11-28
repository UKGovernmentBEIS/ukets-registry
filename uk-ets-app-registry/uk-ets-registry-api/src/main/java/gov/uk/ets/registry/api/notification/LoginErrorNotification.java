package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class LoginErrorNotification extends EmailNotification {

    private String emailAddress;
    private String url;

    @Builder
    public LoginErrorNotification(String emailAddress, String url) {
        super(Set.of(emailAddress), GroupNotificationType.LOGIN_ERROR, null, null, null);
        this.emailAddress = emailAddress;
        this.url = url;
    }

}