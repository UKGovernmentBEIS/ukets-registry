package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if an other pending email change with the same new email exists.
 */
public class OtherPendingEmailChangesWithSameNewEmailShouldNotExist extends AbstractEmailChangeBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public OtherPendingEmailChangesWithSameNewEmailShouldNotExist(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("There is an other pending email change request that uses the same new email address.");
    }

    /**
     * Checks if an other pending email change with the same new email exists.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        EmailChangeSecurityStoreSlice slice = getSlice();
        return slice.isOtherPendingEmailChangeWithSameNewEmailExists() ?  forbiddenOutcome() : Outcome.PERMITTED_OUTCOME;
    }
}
