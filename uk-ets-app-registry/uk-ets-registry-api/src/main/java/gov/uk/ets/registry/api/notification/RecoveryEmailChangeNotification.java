package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class RecoveryEmailChangeNotification extends EmailNotification {

    private String securityCode;

    public RecoveryEmailChangeNotification(String securityCode) {
        super(Set.of(securityCode), GroupNotificationType.RECOVERY_EMAIL_CHANGE_REQUEST, null, null, null);
        this.securityCode = securityCode;
    }
}
