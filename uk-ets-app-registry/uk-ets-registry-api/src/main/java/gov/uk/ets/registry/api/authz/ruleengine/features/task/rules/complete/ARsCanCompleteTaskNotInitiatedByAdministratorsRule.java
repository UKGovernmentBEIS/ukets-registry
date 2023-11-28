package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsCanCompleteTaskNotInitiatedByAdministratorsRule extends AbstractTaskBusinessRule {

    public ARsCanCompleteTaskNotInitiatedByAdministratorsRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            " An AR cannot complete a task that was initiated by a (senior or junior) registry administrator. \n.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        boolean isTaskAssigneeAdministrator =
            getSlice().getTaskAssigneeRoles().stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (isAR) {
            if (isTaskAssigneeAdministrator) {
                return forbiddenOutcome();
            } else {
                return Outcome.PERMITTED_OUTCOME;
            }
        } else {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
    }
}
