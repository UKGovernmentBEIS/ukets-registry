package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserStatus;

import java.util.List;

public class OnlyEnrolledUsersCanCompleteTaskRule extends AbstractTaskBusinessRule {

    private static final List<RequestType> requestTypesAllowedForNonEnrolledUser = List.of(
            RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
            RequestType.AR_REQUESTED_DOCUMENT_UPLOAD
    );

    public OnlyEnrolledUsersCanCompleteTaskRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an Enrolled user can complete this task.");
    }

    @Override
    public Outcome permit() {
        if (UserStatus.ENROLLED.equals(user.getState())) {
            return Outcome.PERMITTED_OUTCOME;
        }
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (requestTypesAllowedForNonEnrolledUser.contains(task.getType())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        return forbiddenOutcome();
    }
}
