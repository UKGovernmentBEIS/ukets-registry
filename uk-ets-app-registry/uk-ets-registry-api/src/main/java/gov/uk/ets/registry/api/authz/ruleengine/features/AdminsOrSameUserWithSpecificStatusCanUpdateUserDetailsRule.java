package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.JUNIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;
import java.util.Arrays;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserStatus;

/**
 * A registry administrator can update the user details of any user of the registry.
 * A user can update only the details of his/her own record, 
 * provided that their status is REGISTERED, VALIDATED or ENROLLED.
 * A user cannot update the user details of another user
 */
public class AdminsOrSameUserWithSpecificStatusCanUpdateUserDetailsRule extends AbstractBusinessRule {

	/**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AdminsOrSameUserWithSpecificStatusCanUpdateUserDetailsRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public Outcome permit() {
        if (hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR) ||
        		user == requestedUser && Arrays.asList(UserStatus.REGISTERED, UserStatus.VALIDATED, UserStatus.ENROLLED).contains(user.getState())) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "A user can perform this action only if he is an administrator or updating his own user details.");
    }
}
