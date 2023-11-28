package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import java.util.List;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;
import lombok.Getter;

/**
 * Abstract business rule for AR update requests.
 */
@Getter
public abstract class AbstractTaskBusinessRule extends AbstractBusinessRule {

    private final TaskBusinessSecurityStoreSlice slice;

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AbstractTaskBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getTaskBusinessSecurityStoreSlice();
    }

    protected Account getAccountOnTask(TaskBusinessRuleInfo ruleInfo) {
        Account account = ruleInfo.getTask().getAccount();
        if (ruleInfo.getTask().getAccount() == null) {
            account = ruleInfo.getTask().getParentTask() != null ?
                      ruleInfo.getTask().getParentTask().getAccount() : null;
        }
        return account;
    }

    protected List<SearchableTransaction> getTransactionOnTask(TaskBusinessRuleInfo ruleInfo) {
        return ruleInfo
            .getTask()
            .getTransactionIdentifiers()
            .stream()
            .map(t -> t.getTransaction())
            .toList();
    }

    protected boolean hasAccessToAccountRelatedTask(Account accountOnTask, boolean isArOrAuthority) {
        if (isArOrAuthority) {
            return accountAccesses.stream()
                                  .anyMatch(aa -> accountOnTask.equals(aa.getAccount()) &&
                                                  !aa.getRight().equals(AccountAccessRight.ROLE_BASED));
        }
        return accountAccesses.stream()
                              .anyMatch(aa -> accountOnTask.equals(aa.getAccount()) &&
                                              aa.getRight().equals(AccountAccessRight.ROLE_BASED));
    }
}
