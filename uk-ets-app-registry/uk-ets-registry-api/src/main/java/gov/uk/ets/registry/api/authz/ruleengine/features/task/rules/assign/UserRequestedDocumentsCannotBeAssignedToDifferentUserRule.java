package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.TaskActionsForUserRequestedDocumentsRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class UserRequestedDocumentsCannotBeAssignedToDifferentUserRule
    extends TaskActionsForUserRequestedDocumentsRule {

    public UserRequestedDocumentsCannotBeAssignedToDifferentUserRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Task cannot be assigned to another user than the applicant");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator =
                userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (isRegistryAdministrator) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (this.getSlice().getTaskAssignee() != null) {
            super.setCheckUserUrid(this.getSlice().getTaskAssignee().getUrid());
            return super.permit();
        }
        return Outcome.PERMITTED_OUTCOME;
    }

}
