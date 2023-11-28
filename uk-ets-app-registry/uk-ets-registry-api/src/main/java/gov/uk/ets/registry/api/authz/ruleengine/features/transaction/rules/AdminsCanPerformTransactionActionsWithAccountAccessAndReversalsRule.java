package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.JUNIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.READONLY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SYSTEM_ADMINISTRATOR;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;


/**
 * This BR checks if administrators have role-base access rights to the transaction-related account.
 * The only exception to this BR is that admins can initiate reversal transactions, regardless their access rights.
 */
public class AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule
    extends AbstractTransactionBusinessRule {

    public AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An administrator cannot perform transaction actions from accounts " +
                  "for which he has no access right to.");
    }

    @Override
    public Outcome permit() {
        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR, SYSTEM_ADMINISTRATOR,
            READONLY_ADMINISTRATOR)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (hasUserAccessToAccount() || isTransactionReversal()) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    private boolean hasUserAccessToAccount() {
        if (getSlice().getTransactionType().isIssuance()) {
            return accountAccesses.stream()
                                  .anyMatch(aa -> aa.getAccount().equals(getSlice().getAcquiringAccount()) &&
                                                  AccountAccessRight.ROLE_BASED.equals(aa.getRight()));
        }
        return accountAccesses.stream()
                              .anyMatch(aa -> aa.getAccount().equals(getSlice().getTransferringAccount()) &&
                                              AccountAccessRight.ROLE_BASED.equals(aa.getRight()));
    }

    private boolean isTransactionReversal() {
        return TransactionType.isReversalTransaction(getSlice().getTransactionType());
    }
}
