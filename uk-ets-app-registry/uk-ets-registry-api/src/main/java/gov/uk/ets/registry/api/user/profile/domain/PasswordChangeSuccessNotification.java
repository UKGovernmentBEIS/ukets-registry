package gov.uk.ets.registry.api.user.profile.domain;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * {@link GroupNotification} for notifying the user for a successful password change.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@Setter
public class PasswordChangeSuccessNotification extends EmailNotification {
    private String email;

    @Builder
    public PasswordChangeSuccessNotification(String email) {
        super(Set.of(email), GroupNotificationType.PASSWORD_CHANGE_SUCCESS, null, null, null);
        this.email = email;
    }

}
