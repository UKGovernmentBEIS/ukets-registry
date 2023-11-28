package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//The has been marked as deprecated due to UKETS-6528
@Deprecated(since = "v3.3.0")
public class ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule extends AbstractTaskBusinessRule {

    public ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Outcome permit() {
        List<ErrorDetail> errorDetailList = new ArrayList<>();
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskOutcome().equals(TaskOutcome.REJECTED)) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            if (!CollectionUtils.isEmpty(taskBusinessRuleInfo.getSubTasks())) {
                Optional<RequestStateEnum> areAnyUncompletedChildTasks = taskBusinessRuleInfo.getSubTasks()
                        .stream()
                        .map(Task::getStatus)
                        .filter(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED::equals)
                        .findAny();
                if (areAnyUncompletedChildTasks.isPresent()) {
                    ErrorDetail errorDetail = ErrorDetail.builder()
                            .message("You must complete all subtasks.")
                            .build();
                    errorDetailList.add(errorDetail);
                }
            }
        }
        if (!errorDetailList.isEmpty()) {
            return Outcome.builder().result(Result.FORBIDDEN)
                    .errorBody(ErrorBody.builder().errorDetails(errorDetailList).build()).failedOnKey(key()).build();
        }
        return Outcome.builder().result(Result.PERMITTED).build();
    }
}
