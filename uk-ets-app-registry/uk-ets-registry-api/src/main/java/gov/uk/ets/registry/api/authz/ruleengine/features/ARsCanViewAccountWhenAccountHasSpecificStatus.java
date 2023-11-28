package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A user cannot view accounts with status SUSPENDED or TRANSFER PENDING or PARTIALLY SUSPENDED.
 */
public class ARsCanViewAccountWhenAccountHasSpecificStatus extends AbstractBusinessRule {

    public ARsCanViewAccountWhenAccountHasSpecificStatus(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("A user cannot view accounts with status SUSPENDED or TRANSFER PENDING or PARTIALLY SUSPENDED.");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR && !userRoles.isEmpty()) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (account.getAccountStatus().equals(AccountStatus.SUSPENDED)
            || account.getAccountStatus().equals(AccountStatus.SUSPENDED_PARTIALLY)
            || account.getAccountStatus().equals(AccountStatus.TRANSFER_PENDING)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
