package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * (AR) An update request cannot be submitted for accounts with status CLOSURE_PENDING or CLOSED or SUSPENDED or TRANSFER PENDING
 * or ALL_TRANSACTIONS_RESTRICTED or SOME_TRANSACTIONS_RESTRICTED or PARTIALLY SUSPENDED.
 */
public class ARsCanSubmitUpdateWhenAccountHasSpecificStatus extends AbstractBusinessRule {

    public ARsCanSubmitUpdateWhenAccountHasSpecificStatus(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("(AR) An update request cannot be submitted for accounts with status CLOSURE_PENDING or CLOSED or SUSPENDED " +
                "or TRANSFER PENDING or PARTIALLY SUSPENDED or PROPOSED");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (account.getAccountStatus().equals(AccountStatus.CLOSURE_PENDING)
            ||account.getAccountStatus().equals(AccountStatus.CLOSED)
            || account.getAccountStatus().equals(AccountStatus.SUSPENDED)
            || account.getAccountStatus().equals(AccountStatus.SUSPENDED_PARTIALLY)
            || account.getAccountStatus().equals(AccountStatus.PROPOSED)
            || account.getAccountStatus().equals(AccountStatus.TRANSFER_PENDING)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
