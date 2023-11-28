package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.apache.commons.lang3.StringUtils;

public class CommentMandatoryWhenApproved extends AbstractTaskBusinessRule {
    public CommentMandatoryWhenApproved(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Enter a reason for approving the request.");
    }

    @Override
    public Outcome permit() {
        TaskOutcome taskOutcome = getSlice().getTaskOutcome();
        String comment = getSlice().getCompleteComment();
            if (TaskOutcome.REJECTED.equals(taskOutcome)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (TaskOutcome.APPROVED.equals(taskOutcome) && StringUtils.isEmpty(comment)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
