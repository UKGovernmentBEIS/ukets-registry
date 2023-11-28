package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rules for checking that the candidate is a suspended AR of the account.
 * This rule is useful for the restore AR request.
 */
public class ARCandidateShouldBeAccountSuspendedAR extends AbstractARBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public ARCandidateShouldBeAccountSuspendedAR(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("The AR Update candidate should be a suspended AR of the account.");
    }

    /**
     * Checks if the candidate is a suspended AR of the account.
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        ARBusinessSecurityStoreSlice slice = getSlice();
        boolean isCandidateSuspendedAR = slice.getAccountARs().stream().filter(ar -> ar.getState().equals(
            AccountAccessState.SUSPENDED))
            .anyMatch(ar -> ar.getUser().getUrid().equals(slice.getCandidateUrid()));
        return isCandidateSuspendedAR ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
