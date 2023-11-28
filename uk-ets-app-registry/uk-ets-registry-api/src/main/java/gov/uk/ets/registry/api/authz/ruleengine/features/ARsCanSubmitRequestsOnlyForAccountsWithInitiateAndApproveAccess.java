package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * User can submit update requests only for the account he/she is the AR with 'initiate & approve'
 * access rights.
 */
public class ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess
    extends ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess {

    public ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "User can submit update requests only for the account he/she is the AR with 'initiate & "
            + "approve' access rights.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        Outcome outcome = super.permit();
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        boolean isAdmin = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if(isAR){
            boolean permitted = accountAccesses.stream()
                    .anyMatch(access ->
                            access.getRight().containsRight(AccountAccessRight.INITIATE_AND_APPROVE)
                    );

            return permitted && outcome == Outcome.PERMITTED_OUTCOME ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();

        } else if(isAdmin){
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
