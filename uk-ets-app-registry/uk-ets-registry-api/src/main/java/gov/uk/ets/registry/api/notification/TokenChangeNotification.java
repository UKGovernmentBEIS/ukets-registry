package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class TokenChangeNotification extends EmailNotification {

    private String emailAddress;
    private String url;

    /**
     * The expiration time in minutes of the email change request.
     */
    private Long expiration;

    @Builder
    public TokenChangeNotification(String emailAddress, String url, Long expiration) {
        super(Set.of(emailAddress), GroupNotificationType.TOKEN_CHANGE_REQUEST, null, null, null);
        this.emailAddress = emailAddress;
        this.url = url;
        this.expiration = expiration;
    }

}
