package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * An update request cannot be submitted by an AR who has been SUSPENDED from the account.
 */
public class ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended extends AbstractBusinessRule {

    public ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An update request cannot be submitted by an AR who has been SUSPENDED from the account.");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        boolean suspended = accountAccesses.stream()
            .filter(access -> access.getAccount().equals(account))
            .filter(access -> access.getUser().equals(user))
            .anyMatch(access -> access.getState().equals(AccountAccessState.SUSPENDED));
        if (suspended) {
            return forbiddenOutcome();
        } else {
            return Outcome.PERMITTED_OUTCOME;
        }
    }
}
