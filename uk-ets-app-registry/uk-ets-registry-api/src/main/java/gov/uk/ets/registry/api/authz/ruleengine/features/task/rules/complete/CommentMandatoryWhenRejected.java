package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.apache.commons.lang3.StringUtils;

public class CommentMandatoryWhenRejected extends AbstractTaskBusinessRule {
    public CommentMandatoryWhenRejected(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Enter a reason for rejecting the request.");
    }

    @Override
    public Outcome permit() {
        TaskOutcome taskOutcome = getSlice().getTaskOutcome();
        String comment = getSlice().getCompleteComment();
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (TaskOutcome.REJECTED.equals(taskOutcome) && StringUtils.isEmpty(comment)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
