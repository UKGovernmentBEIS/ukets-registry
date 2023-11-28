package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A user can only view accounts he/she is the AR.
 */
public class ARsCanViewRequestsOnlyForAccountsWithAccess extends AbstractBusinessRule {

    public ARsCanViewRequestsOnlyForAccountsWithAccess(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("A user can only view accounts he/she is the AR.");
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
        boolean permitted = accountAccesses.stream()
            .filter(access -> access.getAccount().equals(account))
            .filter(access -> access.getUser().equals(user))
            .count() > 0;
        if (permitted) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
