package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.Objects;

public class AdminsOrInitiatorCanViewAccountOpeningFileRule extends AbstractTaskBusinessRule {

    public AdminsOrInitiatorCanViewAccountOpeningFileRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only administrator or initiator can view this task.");
    }

    @Override
    public Outcome permit() {

        boolean isAdmin = userRoles.stream().anyMatch(UserRole::isRegistryAdministrator);
        if (isAdmin) {
            return Outcome.PERMITTED_OUTCOME;
        }

        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (task.getType() != RequestType.ACCOUNT_OPENING_REQUEST) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (Objects.equals(task.getInitiatedBy(), user)) {
            return Outcome.PERMITTED_OUTCOME;
        }

        return forbiddenOutcome();
    }
}
