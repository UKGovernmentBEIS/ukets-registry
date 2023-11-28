package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ReadOnlyAdministratorsCannotSubmitRequest extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public ReadOnlyAdministratorsCannotSubmitRequest(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("A read-only registry administrator cannot submit this request.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministratorReadOnly = userRoles.stream().anyMatch(UserRole::isReadOnlyAdministrator);
        if (isRegistryAdministratorReadOnly) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
