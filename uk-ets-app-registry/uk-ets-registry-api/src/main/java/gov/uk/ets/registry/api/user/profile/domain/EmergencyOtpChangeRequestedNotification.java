package gov.uk.ets.registry.api.user.profile.domain;

import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;

import lombok.*;

/**
 * {@link GroupNotification} for notifying the user for an OTP change requested.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@Setter
public class EmergencyOtpChangeRequestedNotification extends EmailNotification {

    private String email;
    private Long expiration;
    private String verificationUrl;

    @Builder
    public EmergencyOtpChangeRequestedNotification(String email, Long expiration, String verificationUrl) {
        super(Set.of(email), GroupNotificationType.EMERGENCY_OTP_CHANGE_REQUESTED, null, null, null);
        this.email = email;
        this.expiration = expiration;
        this.verificationUrl = verificationUrl;
    }

}
