package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

import static gov.uk.ets.registry.api.user.domain.UserRole.*;

/**
 * A registry administrator can view the details of any user of the registry.
 * A user can view only the details of his/her own record.
 * A user cannot view the user details of another user
 */
public class AdminsOrSameUserCanRequestUserDetailsRule extends AbstractBusinessRule {


    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AdminsOrSameUserCanRequestUserDetailsRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public Outcome permit() {
        if (hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR, READONLY_ADMINISTRATOR) || user == requestedUser) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "A user can perform this action only if he is am administrator or requesting his own user details.");
    }
}
