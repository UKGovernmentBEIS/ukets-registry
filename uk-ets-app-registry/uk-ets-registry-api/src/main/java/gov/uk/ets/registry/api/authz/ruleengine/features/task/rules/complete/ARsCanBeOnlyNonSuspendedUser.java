package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.Set;

public class ARsCanBeOnlyNonSuspendedUser extends AbstractTaskBusinessRule {

    private final Set<RequestType> authorizedRepresentativeAdditionTypes =
        Set.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST);

    public ARsCanBeOnlyNonSuspendedUser(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only non suspended users can become ARs.");
    }

    @Override
    public Outcome permit() {

        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();

        if (!authorizedRepresentativeAdditionTypes.contains(task.getType())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (getSlice().getTaskOutcome() == TaskOutcome.APPROVED && task.getUser().getState() == UserStatus.SUSPENDED) {
            return forbiddenOutcome();
        }

        return Outcome.PERMITTED_OUTCOME;
    }
}
