package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class RequestResetPasswordLinkNotification extends EmailNotification {

    private String emailAddress;
    private String resetPasswordUrl;
    private Long expiration;

    @Builder
    public RequestResetPasswordLinkNotification(String emailAddress, String resetPasswordUrl, Long expiration) {
        super(Set.of(emailAddress), GroupNotificationType.REQUEST_RESET_PASSWORD_LINK, null, null, null);
        this.emailAddress = emailAddress;
        this.resetPasswordUrl = resetPasswordUrl;
        this.expiration = expiration;
    }
}
