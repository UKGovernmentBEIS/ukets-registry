package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Profile("integrationTest")
@Component
public class ReconciliationTestHelper {
    private AccountModelTestHelper accountModelTestHelper;
    private ReconciliationHistoryRepository historyRepository;
    private ReconciliationRepository reconciliationRepository;
    private ReconciliationFailedEntryRepository failedEntryRepository;

    public ReconciliationTestHelper(EntityManager entityManager,
                                    ReconciliationRepository reconciliationRepository,
                                    ReconciliationHistoryRepository historyRepository,
                                    ReconciliationFailedEntryRepository failedEntryRepository
    ) {
        accountModelTestHelper = new AccountModelTestHelper(entityManager);
        this.reconciliationRepository = reconciliationRepository;
        this.historyRepository = historyRepository;
        this.failedEntryRepository = failedEntryRepository;
    }

    @Builder
    public static class TestCase {
        private boolean uktlShouldReturnFailures;
        private List<Long> blockedAccounts;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initData(TestCase testCase) {
        if (!testCase.uktlShouldReturnFailures) {
            return;
        }
        AccountHolder holder = accountModelTestHelper.addAccountHolder(AddAccountHolderCommand
            .builder()
            .accountHolderType(AccountHolderType.INDIVIDUAL)
            .identifier(1000L)
            .status(Status.ACTIVE)
            .name("TEST 1")
            .build());

        testCase.blockedAccounts.stream().forEach(identifier -> {
            accountModelTestHelper.addAccount(AddAccountCommand.
                builder()
                .accountHolder(holder)
                .accountId(identifier)
                .accountName("transferring account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .fullIdentifier("GB-213-213-213")
                .kyotoAccountType(KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT)
                .registryAccountType(RegistryAccountType.NATIONAL_HOLDING_ACCOUNT)
                .build());
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createInitiatedReconciliation(Long identifier) {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setStatus(ReconciliationStatus.INITIATED);
        reconciliation.setCreated(new Date());
        reconciliation.setIdentifier(identifier);
        reconciliationRepository.saveAndFlush(reconciliation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearAll() {
        historyRepository.deleteAll();
        failedEntryRepository.deleteAll();
        reconciliationRepository.deleteAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doInNewTransaction(Runnable runnable) {
        runnable.run();
    }
}
