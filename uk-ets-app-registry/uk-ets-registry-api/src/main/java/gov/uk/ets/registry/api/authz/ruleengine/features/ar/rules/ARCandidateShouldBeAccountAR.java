package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Business rule that checks if the AR candidate is an AR of the account.
 */
public class ARCandidateShouldBeAccountAR extends AbstractARBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public ARCandidateShouldBeAccountAR(
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
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        ARBusinessSecurityStoreSlice slice = getSlice();
        boolean isCandidateAnAR = slice.getAccountARs().stream()
            .anyMatch(ar -> ar.getUser().getUrid().equals(slice.getCandidateUrid()));
        return isCandidateAnAR ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
