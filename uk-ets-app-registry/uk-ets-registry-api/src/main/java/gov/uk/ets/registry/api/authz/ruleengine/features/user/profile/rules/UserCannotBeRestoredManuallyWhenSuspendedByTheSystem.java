package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.StatusChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if the user has been suspended by the system.
 * 
 * If the suspension has been raised by the system (and not by the user), the user is not allowed to restore the User status manually.
 * 
 */
public class UserCannotBeRestoredManuallyWhenSuspendedByTheSystem extends AbstractStatusChangeBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public UserCannotBeRestoredManuallyWhenSuspendedByTheSystem(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        //See JIRA UKETS-5914
        return ErrorBody.from("You cannot restore a user manually who is suspended by the system. There is request pending approval.");
    }

    /**
     * Checks if the user has been suspended by the system.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        StatusChangeSecurityStoreSlice slice = getSlice();
        return slice.isUserSuspendedByTheSystem() ? forbiddenOutcome() : Outcome.PERMITTED_OUTCOME;
    }
}
