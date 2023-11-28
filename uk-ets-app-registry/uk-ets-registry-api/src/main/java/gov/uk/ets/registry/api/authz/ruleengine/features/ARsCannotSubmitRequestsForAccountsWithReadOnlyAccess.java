package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * User can submit update requests only for the account he/she is the AR with 'initiate' or 'initiate & approve' or 'surrender'
 * access rights.
 */
public class ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess extends AbstractBusinessRule {

    public ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "User cannot submit update requests only for the account he/she is the AR with " +
            "read-only access rights.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR && !userRoles.isEmpty()) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }

        if (accountAccesses.stream()
                           .filter(access -> access.getState().equals(AccountAccessState.ACTIVE))
                           .filter(access -> access.getAccount().equals(account))
                           .filter(access -> access.getUser().equals(user))
                           .noneMatch(access -> access.getRight() == AccountAccessRight.READ_ONLY)) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
