package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PublicAccountIdentifierMigrator implements Migrator {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {

        log.info("PublicAccountIdentifierMigrator");

        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(
                MigratorName.PUBLIC_ACCOUNT_IDENTIFIER_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[PublicAccountIdentifierMigrator] has already been performed, skipping.");
            return;
        }

        List<Account> accountsWithoutPublicID = accountRepository.findByPublicIdentifierIsNull();

        if (accountsWithoutPublicID.isEmpty()) {
            log.info("[PublicAccountIdentifierMigrator] No accounts without a public identifier found.");
            return;
        }

        accountsWithoutPublicID.forEach(account ->
            account.setPublicIdentifier(accountService.generateUniquePublicIdentifier()));

        accountRepository.saveAll(accountsWithoutPublicID);

        updateMigrationHistory();
        log.info("[PublicAccountIdentifierMigrator] completed for {} accounts.", accountsWithoutPublicID.size());
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.PUBLIC_ACCOUNT_IDENTIFIER_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}

