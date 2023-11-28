package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import lombok.Getter;
import lombok.Setter;

/**
 * The data that are used on user status change business rules checks.
 */
@Getter
@Setter
public class StatusChangeSecurityStoreSlice {

    /**
     * Flag that indicates the existence of a pending request triggered by the system, resulting in the suspension of the user.
     */
    private boolean userSuspendedByTheSystem;
}
