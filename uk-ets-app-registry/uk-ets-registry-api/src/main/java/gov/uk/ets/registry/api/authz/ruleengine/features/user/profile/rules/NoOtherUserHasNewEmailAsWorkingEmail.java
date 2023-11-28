package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if the new email belongs to other user.
 */
public class NoOtherUserHasNewEmailAsWorkingEmail extends AbstractEmailChangeBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public NoOtherUserHasNewEmailAsWorkingEmail(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        //See JIRA UKETS-3596
        //Display an error message that is the same with another one (in this case not matching password) 
        //to avoid unauthenticated user enumeration flaw.
        return ErrorBody.from("The emails did not match");
    }

    /**
     * Checks if the new email belongs to other user.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        EmailChangeSecurityStoreSlice slice = getSlice();
        return slice.isOtherUsersEmail() ?  forbiddenOutcome() : Outcome.PERMITTED_OUTCOME;
    }
}
