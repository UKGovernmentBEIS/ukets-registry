package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

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
 * A senior registry administrator can complete tasks where the initiator is an administrator.
 */
public class SeniorAdminCanClaimTaskInitiatedByAdminRule
    extends AbstractTaskBusinessRule {

    public static final String
        TASK_SHALL_ONLY_BE_CLAIMED_BY_A_SENIOR_ADMINISTRATOR_SINCE_IT_WAS_INITIATED_BY_AN_ADMINISTRATOR =
        "Task with request id: %s shall only be claimed by a senior administrator since it was initiated by " +
            "an administrator.";

    public SeniorAdminCanClaimTaskInitiatedByAdminRule(
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
        TaskBusinessSecurityStoreSlice slice = getSlice();
        List<ErrorDetail> errorsDetails = new ArrayList<>();
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            boolean isInitiatorAdministrator =
                taskBusinessRuleInfo.getTaskInitiatorRoles().stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
            if (isInitiatorAdministrator) {
                ErrorDetail errorDetail = ErrorDetail.builder().message(String.format(
                    TASK_SHALL_ONLY_BE_CLAIMED_BY_A_SENIOR_ADMINISTRATOR_SINCE_IT_WAS_INITIATED_BY_AN_ADMINISTRATOR,
                    taskBusinessRuleInfo.getTask().getRequestId()))
                    .build();
                errorsDetails.add(errorDetail);
            }
        }
        if (!errorsDetails.isEmpty()) {
            return Outcome.builder().result(Result.FORBIDDEN)
                .errorBody(ErrorBody.builder().errorDetails(errorsDetails).build()).failedOnKey(key()).build();
        }
        return Outcome.builder().result(Result.PERMITTED).build();
    }
}
