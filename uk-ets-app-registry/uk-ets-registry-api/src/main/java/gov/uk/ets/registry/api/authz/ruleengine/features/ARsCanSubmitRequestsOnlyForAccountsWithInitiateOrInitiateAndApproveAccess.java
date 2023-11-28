package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * User can submit update requests only for the account he/she is the AR with 'initiate' or 'initiate & approve'
 * access rights.
 */
public class ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess extends AbstractBusinessRule {

    public ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "User can submit update requests only for the account he/she is the AR with 'initiate' or 'initiate & "
                + "approve access rights.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        boolean permitted = accountAccesses.stream()
            .filter(access -> access.getState().equals(AccountAccessState.ACTIVE))
            .filter(access -> access.getAccount().equals(account))
            .filter(access -> access.getUser().equals(user))
            .anyMatch(access -> access.getRight().containsRight(AccountAccessRight.INITIATE)
            );
        if (permitted) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
