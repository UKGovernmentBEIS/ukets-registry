package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;

/**
 * An update request cannot be submitted by an AR user whose status is NOT ENROLLED.
 */
public class ARsCanSubmitUpdateWhenUserStatusIsEnrolled extends AbstractBusinessRule {


    public ARsCanSubmitUpdateWhenUserStatusIsEnrolled(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("An update request cannot be submitted by an AR user whose status is NOT ENROLLED");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR && !userRoles.isEmpty()) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (UserStatus.ENROLLED.equals(user.getState())) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
