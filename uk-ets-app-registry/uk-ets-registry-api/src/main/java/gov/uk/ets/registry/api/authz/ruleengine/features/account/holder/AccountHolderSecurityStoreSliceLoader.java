package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder;

import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountHolderSecurityStoreSliceLoader {

    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;
    private final AccountHolderRepository accountHolderRepository;
    private final TaskRepository taskRepository;

    /**
     * Loads the {@link AccountHolderSecurityStoreSlice account holder store slice}.
     */
    public void load() {
        if (businessSecurityStore.getAccountHolderSecurityStoreSlice() != null ||
            !ruleInputStore.containsKey(RuleInputType.ACCOUNT_HOLDER_ID)) {
            return;
        }

        AccountHolderSecurityStoreSlice accountHolderSecurityStoreSlice = new AccountHolderSecurityStoreSlice();
        Long accountHolderId = (Long) ruleInputStore.get(RuleInputType.ACCOUNT_HOLDER_ID);
        accountHolderSecurityStoreSlice.setAccountHolder(accountHolderRepository.getAccountHolder(accountHolderId));
        if (ruleInputStore.containsKey(RuleInputType.ACCOUNT_HOLDER_ID)) {
            accountHolderSecurityStoreSlice.setTasksByAccountHolder(
                taskRepository.countPendingTasksByAccountIdentifierInAndType(accountHolderId));
        }
        businessSecurityStore.setAccountHolderSecurityStoreSlice(accountHolderSecurityStoreSlice);
    }

    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }
}
