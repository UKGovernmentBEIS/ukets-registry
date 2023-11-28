package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if the AR candidate is an active AR of the account.
 * This rule is useful for the remove, suspend and Change AR access rights requests.
 */
public class ARCandidateShouldBeAccountActiveAR extends AbstractARBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public ARCandidateShouldBeAccountActiveAR(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("The AR Update candidate should be an active AR of the account.");
    }

    /**
     * Checks if the candidate is an active AR of the account.
     * @return The {@link gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome} outcome
     */
    @Override
    public Outcome permit() {
        ARBusinessSecurityStoreSlice slice = getSlice();
        boolean isCandidateActiveAR = slice.getAccountARs().stream().filter(ar -> ar.getState().equals(AccountAccessState.ACTIVE))
            .anyMatch(ar -> ar.getUser().getUrid().equals(slice.getCandidateUrid()));
        return isCandidateActiveAR ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
