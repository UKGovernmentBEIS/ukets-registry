package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AbstractTransactionBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class AuthoritiesCanViewOnlyTransactionsOfAccountsWithAccessRule extends AbstractTransactionBusinessRule {

    public AuthoritiesCanViewOnlyTransactionsOfAccountsWithAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user can only view transactions for which he/she has Account Access " +
                "to the corresponding transferring or acquiring accounts.");
    }

    @Override
    public Outcome permit() {
        if (!hasRole(AUTHORITY_USER)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (hasUserAccessToAccountExceptRoleBased()) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    private boolean hasUserAccessToAccountExceptRoleBased() {
        return accountAccesses.stream()
            .anyMatch(
                accountAccessToAccountExceptRoleBased(getSlice().getTransferringAccount())
                    .or(accountAccessToAccountExceptRoleBased(getSlice().getAcquiringAccount()))
            );
    }

    @NotNull
    private Predicate<AccountAccess> accountAccessToAccountExceptRoleBased(Account account) {
        return aa -> aa.getAccount().equals(account) &&
            !aa.getRight().equals(AccountAccessRight.ROLE_BASED);
    }
}
