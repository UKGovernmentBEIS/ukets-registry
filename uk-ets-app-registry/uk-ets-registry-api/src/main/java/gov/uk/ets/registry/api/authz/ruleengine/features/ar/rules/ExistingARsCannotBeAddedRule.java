package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class ExistingARsCannotBeAddedRule extends AbstractARBusinessRule {

    public ExistingARsCannotBeAddedRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("The user is already added as an authorised representative on this account");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        if (getSlice().getAccountARs().stream().anyMatch(ar -> ar.getUser().getUrid().equals(getSlice().getCandidateUrid()))) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }

}
