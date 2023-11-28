package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;


/**
 * This BR checks if authority users have role-base access rights to the transaction-related account.
 */
public class AuthorityCanPerformTransactionActionsWithAccountAccessRule
    extends AbstractTransactionBusinessRule {

    public AuthorityCanPerformTransactionActionsWithAccountAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user cannot perform transaction actions from accounts " +
                  "for which he has no access right to.");
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
        if (getSlice().getTransactionType().isIssuance()) {
            return accountAccesses.stream()
                                  .anyMatch(aa -> aa.getAccount().equals(getSlice().getAcquiringAccount()) &&
                                                  !AccountAccessRight.ROLE_BASED.equals(aa.getRight()));
        }
        return accountAccesses.stream()
                              .anyMatch(aa -> aa.getAccount().equals(getSlice().getTransferringAccount()) &&
                                              !AccountAccessRight.ROLE_BASED.equals(aa.getRight()));
    }
}
