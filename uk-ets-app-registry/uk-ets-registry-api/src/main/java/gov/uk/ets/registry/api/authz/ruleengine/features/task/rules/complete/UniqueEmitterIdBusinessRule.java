package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;

public class UniqueEmitterIdBusinessRule extends AbstractTaskBusinessRule {

    private final Boolean emitterIdExists;

    public UniqueEmitterIdBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        emitterIdExists = businessSecurityStore.getEmitterIdExists();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("This task cannot be approved as this emitter ID is used by another account");
    }

    @Override
    public BusinessRule.Outcome permit() {

        if (getSlice().getTaskOutcome() == TaskOutcome.REJECTED) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (Boolean.TRUE.equals(emitterIdExists)) {
            return forbiddenOutcome();
        }

        return BusinessRule.Outcome.PERMITTED_OUTCOME;
    }
}
