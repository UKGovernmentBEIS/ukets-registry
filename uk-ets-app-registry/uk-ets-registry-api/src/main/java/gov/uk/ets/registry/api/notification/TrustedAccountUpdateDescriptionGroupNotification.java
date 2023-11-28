package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Extends a {@link GroupNotification} with transaction related data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrustedAccountUpdateDescriptionGroupNotification extends EmailNotification {
    private String accountIdentifier;
    private String accountFullIdentifier;
    private String description;
    @Builder
    public TrustedAccountUpdateDescriptionGroupNotification(Set<String> recipients, GroupNotificationType type,
                                                            String accountIdentifier, String accountFullIdentifier,
                                                            String description) {
        super(recipients, type, null, null, null);
        this.accountFullIdentifier = accountFullIdentifier;
        this.accountIdentifier = accountIdentifier;
        this.description = description;
    }
}
