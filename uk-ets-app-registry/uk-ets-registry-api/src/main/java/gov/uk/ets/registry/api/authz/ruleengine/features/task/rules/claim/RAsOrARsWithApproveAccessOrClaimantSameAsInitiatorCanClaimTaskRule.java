package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.BaseTransactionEntity;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule extends AbstractTaskBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("You do not have permission to perform this action");
    }

    @Override
    public Outcome permit() {
        for (TaskBusinessRuleInfo taskInfo : getSlice().getTaskBusinessRuleInfoList()) {
            if (!userScopes.contains(Scope.SCOPE_ACTION_ANY_ADMIN.getScopeName())) {
                if (!userScopes.contains(
                    Scope.SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE.getScopeName())) {
                    return forbiddenOutcome();
                } else {
                    if (!isClaimantSameAsInitiator(taskInfo.getTask(), user) &&
                        !isClaimantArInAccountWithApproveAccess(taskInfo.getTask().getAccount())) {
                        return forbiddenOutcome();
                    }
                }
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    private boolean isClaimantArInAccountWithApproveAccess(Account account) {
        if (isClaimantArInAccountWithApproveAccess(account, AccountAccessRight.APPROVE)) {
            return true;
        }

        List<TransactionType> transactionTypes = getSlice().getTaskBusinessRuleInfoList()
            .stream()
            .map(this::getTransactionOnTask)
            .flatMap(Collection::stream)
            .map(BaseTransactionEntity::getType)
            .filter(Objects::nonNull)
            .toList();

        boolean validForSurrenderAR = transactionTypes.isEmpty() ||
            transactionTypes.stream().allMatch(TransactionType::isOptionAvailableToSurrenderAR);

        if (validForSurrenderAR) {
            return isClaimantArInAccountWithApproveAccess(account, AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        }

        return false;
    }

    private boolean isClaimantArInAccountWithApproveAccess(Account account, AccountAccessRight right) {
        return accountAccesses.stream()
            .filter(access -> access.getState().equals(AccountAccessState.ACTIVE))
            .filter(access -> access.getAccount().equals(account))
            .anyMatch(access -> access.getRight().containsRight(right));
    }

    private boolean isClaimantSameAsInitiator(Task task, User currentUser) {
        return task.getInitiatedBy().equals(currentUser);
    }
}
