package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if an other pending email change has been initiated by the same user.
 */
public class OtherPendingEmailChangesOfUserOfUridShouldNotExist extends AbstractEmailChangeBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public OtherPendingEmailChangesOfUserOfUridShouldNotExist(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("There is an other pending email change request initiated by this user.");
    }

    /**
     * Checks if an other pending email change has been initiated by the same user.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        EmailChangeSecurityStoreSlice slice = getSlice();
        return slice.isOtherPendingEmailChangeByCurrentUserExists() ?  forbiddenOutcome() : Outcome.PERMITTED_OUTCOME;
    }
}
