package gov.uk.ets.registry.api.authz.ruleengine.features.transaction;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionBusinessSecurityStoreSliceLoader {
    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;
    private final TransactionPersistenceService transactionPersistenceService;
    private final AccountRepository accountRepository;

    /**
     * Loads the {@link TransactionBusinessSecurityStoreSlice}.
     */
    public void load() {
        if (businessSecurityStore.getTransactionBusinessSecurityStoreSlice() != null
            || !ruleInputStore.containsKey(RuleInputType.TRANSACTION_TYPE)
            && !ruleInputStore.containsKey(RuleInputType.TRANSACTION)
            && !ruleInputStore.containsKey(RuleInputType.TRANSACTION_IDENTIFIER)) {
            return;
        }
        TransactionBusinessSecurityStoreSlice transactionBusinessSecurityStoreSlice =
            new TransactionBusinessSecurityStoreSlice();

        TransactionType transactionType;
        if (ruleInputStore.containsKey(RuleInputType.TRANSACTION_TYPE)) {
            transactionType = (TransactionType) ruleInputStore.get(RuleInputType.TRANSACTION_TYPE);
        } else if (ruleInputStore.containsKey(RuleInputType.TRANSACTION)) {
            TransactionSummary transaction = ruleInputStore.get(RuleInputType.TRANSACTION, TransactionSummary.class);
            transactionType = transaction.getType();
            Optional<Account> transferringAccount =
                accountRepository.findByIdentifier(transaction.getTransferringAccountIdentifier());
            transferringAccount.ifPresent(transactionBusinessSecurityStoreSlice::setTransferringAccount);
            Optional<Account> acquiringAccount =
                accountRepository.findByIdentifier(transaction.getAcquiringAccountIdentifier());
            acquiringAccount.ifPresent(transactionBusinessSecurityStoreSlice::setAcquiringAccount);
            transactionBusinessSecurityStoreSlice.setComment(transaction.getComment());
        } else {
            String transactionIdentifier = ruleInputStore.get(RuleInputType.TRANSACTION_IDENTIFIER, String.class);
            Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
            transactionType = transaction.getType();
            Optional<Account> transferringAccount =
                accountRepository.findByFullIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier());
            Optional<Account> acquiringAccount =
                accountRepository.findByFullIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier());
            transferringAccount.ifPresent(transactionBusinessSecurityStoreSlice::setTransferringAccount);
            acquiringAccount.ifPresent(transactionBusinessSecurityStoreSlice::setAcquiringAccount);
        }
        transactionBusinessSecurityStoreSlice.setTransactionType(transactionType);
        businessSecurityStore.setTransactionBusinessSecurityStoreSlice(transactionBusinessSecurityStoreSlice);
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
