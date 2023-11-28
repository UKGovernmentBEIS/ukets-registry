package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.function.Predicate;

/**
 * A user can view only transaction details of accounts he/she is the AR.
 */
public class ARsCanViewOnlyTransactionsOfAccountsWithAccess extends AbstractTransactionBusinessRule {

    public ARsCanViewOnlyTransactionsOfAccountsWithAccess(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("A user can only view transaction details of accounts he/she is the AR.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR && !userRoles.isEmpty()) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        boolean permitted = accountAccesses.stream()
                                           .filter(isArInEitherTransferringOrAcquiringAccount())
                                           .anyMatch(access -> access.getUser().equals(user));
        if (permitted) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }

    private Predicate<AccountAccess> isArInEitherTransferringOrAcquiringAccount() {
        Predicate<AccountAccess> predicate = aa -> aa.getAccount().equals(getSlice().getTransferringAccount());
        return predicate.or(aa -> aa.getAccount().equals(getSlice().getAcquiringAccount()))
                        .and(aa -> AccountAccessState.ACTIVE.equals(aa.getState()));
    }
}
