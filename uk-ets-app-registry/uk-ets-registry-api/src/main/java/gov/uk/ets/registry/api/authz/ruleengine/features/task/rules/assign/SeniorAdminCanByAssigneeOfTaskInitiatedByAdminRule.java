package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 * Task shall only be assigned to a senior admin when the initiator is an administrator.
 */
public class SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule
    extends AbstractTaskBusinessRule {

    public SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Outcome permit() {

        boolean isUserSeniorRegistryAdministrator =
            userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        if (isUserSeniorRegistryAdministrator) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        List<ErrorDetail> errorDetailList = new ArrayList<>();
        TaskBusinessSecurityStoreSlice slice = getSlice();
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            boolean isInitiatorAdministrator =
                taskBusinessRuleInfo.getTaskInitiatorRoles().stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
            if (isInitiatorAdministrator) {
                ErrorDetail errorDetail = ErrorDetail.builder().message(String.format(
                    "Task %s shall only be assigned by a senior administrator since it was initiated by an " +
                        "administrator.",
                    taskBusinessRuleInfo.getTask().getRequestId()))
                    .build();
                errorDetailList.add(errorDetail);
            }
        }
        if (!errorDetailList.isEmpty()) {
            return Outcome.builder().result(Result.FORBIDDEN)
                .errorBody(ErrorBody.builder().errorDetails(errorDetailList).build()).failedOnKey(key()).build();
        }
        return Outcome.builder().result(Result.PERMITTED).build();
    }
}
