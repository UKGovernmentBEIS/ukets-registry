package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;

public class CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule extends AbstractBusinessRule {

    public CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("The selected account has already been added to the Trusted Account List.");
    }

    @Override
    public Outcome permit() {
        if (trustedAccounts == null || trustedAccountCandidate == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (trustedAccounts.stream().anyMatch(acc -> acc.getTrustedAccountFullIdentifier().equals(
                trustedAccountCandidate.getFullIdentifier()) 
                && TrustedAccountStatus.ACTIVE.equals(acc.getStatus()))) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
