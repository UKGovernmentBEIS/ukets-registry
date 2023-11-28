package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A (senior or junior) registry administrator can submit update requests for all end-user accounts in the registry
 * (but not for government accounts).
 */
public class RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount extends AbstractBusinessRule {

    public RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("A (senior or junior) registry administrator can submit update requests for all " +
            "end-user accounts in the registry (but not for government accounts)");
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
            if (Boolean.TRUE.equals(account.getKyotoAccountType().isGovernment()) &&
                RegistryAccountType.NONE.equals(account.getRegistryAccountType()) ||
                Boolean.TRUE.equals(account.getRegistryAccountType().isGovernment())) {
                return forbiddenOutcome();
            }
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
    }
}
