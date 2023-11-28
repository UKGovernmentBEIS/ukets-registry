package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed extends AbstractBusinessRule {

    public ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("A TAL update cannot be submitted for candidate account with invalid account status.");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        // If the trusted account (not found in Registry DB) is null then the account is external.
        if (!isAR || trustedAccountCandidate == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (AccountStatus.CLOSED.equals(trustedAccountCandidate.getAccountStatus())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
