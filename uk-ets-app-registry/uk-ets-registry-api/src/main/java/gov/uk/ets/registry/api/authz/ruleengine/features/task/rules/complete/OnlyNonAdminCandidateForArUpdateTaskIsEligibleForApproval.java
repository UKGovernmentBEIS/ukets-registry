package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.function.Predicate;

/**
 * Only an AR update task for a non-admin candidate user can be approved.
 */
public class OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval
    extends AbstractTaskBusinessRule {

    public OnlyNonAdminCandidateForArUpdateTaskIsEligibleForApproval(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "Only a non-admin candidate user can be added as an AR.");
    }

    @Override
    public Outcome permit() {
        if (TaskOutcome.REJECTED.equals(getSlice().getTaskOutcome())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (getSlice().getTaskBusinessRuleInfoList()
                      .stream()
                      .map(tri -> tri.getTask().getType())
                      .anyMatch(Predicate.not(isArAdditionOrReplacement()))) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        boolean isRegistryAdministrator = getSlice().getCandidateUserRoles()
                                                    .stream()
                                                    .anyMatch(UserRole::isRegistryAdministrator);
        if (isRegistryAdministrator) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    private Predicate<RequestType> isArAdditionOrReplacement() {
        return rt -> RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST.equals(rt) ||
                     RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST.equals(rt);
    }
}
