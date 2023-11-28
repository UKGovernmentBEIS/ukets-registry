package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;

import java.util.List;

public class ARsCanViewSpecificTaskTypes extends AbstractTaskBusinessRule {
    private static final List<RequestType> requestTypesProhibitedForViewByARs = RequestType.getTasksNotDisplayedToAR();

    public ARsCanViewSpecificTaskTypes(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("A User cannot view the specific task type.");
    }

    @Override
    public Outcome permit() {
        boolean isAdmin = userRoles.stream().anyMatch(UserRole::isRegistryAdministrator);
        if (isAdmin) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        boolean isAuthority = userRoles.stream().anyMatch(UserRole::isAuthority);
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (requestTypesProhibitedForViewByARs.contains(task.getType()) && (isAR || isAuthority)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
