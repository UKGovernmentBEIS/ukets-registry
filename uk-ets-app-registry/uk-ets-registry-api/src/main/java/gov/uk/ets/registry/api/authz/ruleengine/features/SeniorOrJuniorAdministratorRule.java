package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Only a SRA or JRA can perform the requested action.
 */
public class SeniorOrJuniorAdministratorRule extends AbstractBusinessRule {
    
	public SeniorOrJuniorAdministratorRule(
            BusinessSecurityStore businessSecurityStore) {
            super(businessSecurityStore);
        }

        /**
         * Refer to {@link BusinessRule#error()}.
         */
        @Override
        public ErrorBody error() {
            return ErrorBody.from("Only a senior or junior registry administrator can perform the requested action");
        }

        /**
         * Returns the permission result.
         *
         * @return The permission result
         */
        @Override
        public Outcome permit() {
            boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
            if (isRegistryAdministrator) {
                return Outcome.PERMITTED_OUTCOME;
            } else {
                return forbiddenOutcome();
            }
        }
}
