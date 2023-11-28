package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.EnrolledAROrSeniorRegistryAdminRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class CanBeCompletedByEnrolledAROrSeniorRegistryAdminRule extends EnrolledAROrSeniorRegistryAdminRule {

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an Enrolled Authorised Representative or a Senior Registry Administrator can complete this task.");
    }

    public CanBeCompletedByEnrolledAROrSeniorRegistryAdminRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }
}
