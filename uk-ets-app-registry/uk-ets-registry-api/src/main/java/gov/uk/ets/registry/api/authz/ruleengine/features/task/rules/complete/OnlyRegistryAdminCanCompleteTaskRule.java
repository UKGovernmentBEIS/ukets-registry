package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Only a registry administrator (junior or senior) can complete this task.
 */
public class OnlyRegistryAdminCanCompleteTaskRule extends AbstractTaskBusinessRule {
	
	public OnlyRegistryAdminCanCompleteTaskRule(
	        BusinessSecurityStore businessSecurityStore) {
	        super(businessSecurityStore);
	    }

	    @Override
	    public ErrorBody error() {
	        return ErrorBody
	            .from(
	                "Only a registry administrator can approve this task.");
	    }

	    @Override
	    public Outcome permit() {
	        TaskBusinessSecurityStoreSlice slice = getSlice();
	        if (TaskOutcome.REJECTED.equals(slice.getTaskOutcome())) {
	            return Outcome.NOT_APPLICABLE_OUTCOME;
	        }
	        boolean isSeniorRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
	        boolean isJuniorRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isJuniorAdministrator);
	        if (!(isSeniorRegistryAdministrator || isJuniorRegistryAdministrator)) {
	            return forbiddenOutcome();
	        }
	        return Outcome.PERMITTED_OUTCOME;
	    }

}
