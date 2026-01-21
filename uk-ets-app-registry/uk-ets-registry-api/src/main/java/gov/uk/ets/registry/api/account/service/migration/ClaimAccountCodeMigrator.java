package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountGeneratorService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClaimAccountCodeMigrator implements Migrator {

    private final AccountGeneratorService generatorService;
    private final AccountRepository accountRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {

        log.info("ClaimAccountCodeMigrator");

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.CLAIM_ACCOUNT_CODE_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[ClaimAccountCodeMigrator] has already been performed, skipping.");
            return;
        }

        List<Account> accountsWithoutClaimAccountCode = accountRepository.findByAccountClaimCodeIsNull();

        if (accountsWithoutClaimAccountCode.isEmpty()) {
            log.info("[ClaimAccountCodeMigrator] No accounts without a claim account code found.");
            return;
        }

        accountsWithoutClaimAccountCode.forEach(account -> {
                    if (account.getAccountClaimCode() == null) {
                        account.setAccountClaimCode(generateAccountClaimCode());
                    }
                }
        );

        accountRepository.saveAll(accountsWithoutClaimAccountCode);

        updateMigrationHistory();
        log.info("[ClaimAccountCodeMigrator] completed for {} accounts.", accountsWithoutClaimAccountCode.size());
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.CLAIM_ACCOUNT_CODE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }

    private String generateAccountClaimCode() {
        try {
            return generatorService.generateAccountClaimCode();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate account claim code", e);
        }
    }
}
