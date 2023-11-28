package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * (Registry administrator) An update request cannot be submitted for accounts with status CLOSURE PENDING or CLOSED.
 */
public class RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus extends AbstractBusinessRule {

    public RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "(Registry administrator) An update request cannot be submitted for accounts with status CLOSURE PENDING " +
                "or CLOSED.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (!isRegistryAdministrator) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (account.getAccountStatus().equals(AccountStatus.CLOSURE_PENDING)
            || account.getAccountStatus().equals(AccountStatus.CLOSED)
            || account.getAccountStatus().equals(AccountStatus.PROPOSED)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
