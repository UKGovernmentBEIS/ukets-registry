package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import lombok.Getter;
import lombok.Setter;

/**
 * The data that are used on email change business rules checks.
 */
@Getter
@Setter
public class EmailChangeSecurityStoreSlice {

    /**
     * Flag that indicates that other user has the new email as his/her working email address.
     */
    private boolean otherUsersEmail;

    /**
     * Flag that indicates that an other pending email changes initiated by current user exist.
     */
    private boolean otherPendingEmailChangeByCurrentUserExists;

    /**
     * Flag that indicates that an other pending email changes initiated by user exist.
     */
    private boolean otherPendingEmailChangeByUserExists;

    /**
     * Flag that indicates that an other pending email change which uses the same new email address exist.
     */
    private boolean otherPendingEmailChangeWithSameNewEmailExists;
}
