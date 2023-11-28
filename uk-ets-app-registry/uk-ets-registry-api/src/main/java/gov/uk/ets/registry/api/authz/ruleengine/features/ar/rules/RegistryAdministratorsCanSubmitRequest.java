package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Business rule that instructs that only the senior or junior registry administrator can submit restore AR or suspend
 * AR requests for all end-user accounts in the registry (but not for government accounts).
 */
public class RegistryAdministratorsCanSubmitRequest extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public RegistryAdministratorsCanSubmitRequest(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only senior or junior registry administrators can submit restore AR or suspend AR requests.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        return isRegistryAdministrator ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
