package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;

public class EnrolledAROrSeniorRegistryAdminRule extends AbstractTaskBusinessRule {

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an Enrolled Authorised Representative or a Senior Registry Administrator can perform this action.");
    }

    public EnrolledAROrSeniorRegistryAdminRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (isAR && UserStatus.ENROLLED.equals(user.getState())) {
            return Outcome.PERMITTED_OUTCOME;
        }
        boolean isUserSeniorRegistryAdministrator =
                userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        if (isUserSeniorRegistryAdministrator) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }
}