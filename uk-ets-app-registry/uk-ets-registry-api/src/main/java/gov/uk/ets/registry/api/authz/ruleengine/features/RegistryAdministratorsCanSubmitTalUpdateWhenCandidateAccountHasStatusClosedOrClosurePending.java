package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending
    extends AbstractBusinessRule {

    public RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("A TAL update cannot be submitted for candidate account with " +
                  "CLOSURE PENDING or CLOSED account status.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        // If the trusted account (not found in Registry DB) is null then the account is external.
        if (!isRegistryAdministrator || trustedAccountCandidate == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (AccountStatus.CLOSURE_PENDING.equals(trustedAccountCandidate.getAccountStatus()) ||
            AccountStatus.CLOSED.equals(trustedAccountCandidate.getAccountStatus())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
