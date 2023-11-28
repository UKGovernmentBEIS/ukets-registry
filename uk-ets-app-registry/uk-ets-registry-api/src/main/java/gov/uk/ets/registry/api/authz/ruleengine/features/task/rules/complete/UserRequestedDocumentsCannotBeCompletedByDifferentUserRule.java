package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.TaskActionsForUserRequestedDocumentsRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class UserRequestedDocumentsCannotBeCompletedByDifferentUserRule extends TaskActionsForUserRequestedDocumentsRule {

    public UserRequestedDocumentsCannotBeCompletedByDifferentUserRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Task cannot be completed by another user than the applicant");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator =
                userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (isRegistryAdministrator) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        super.setCheckUserUrid(this.user.getUrid());
        return super.permit();
    }
}
