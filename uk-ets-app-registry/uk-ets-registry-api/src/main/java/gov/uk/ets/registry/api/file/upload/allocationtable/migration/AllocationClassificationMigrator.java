package gov.uk.ets.registry.api.file.upload.allocationtable.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.file.upload.allocationtable.services.AllocationTableService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class AllocationClassificationMigrator implements Migrator {

    private final MigratorHistoryRepository repository;
    private final AllocationTableService allocationTableService;
    private final CompliantEntityRepository compliantEntityRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void migrate() {
        log.info("Starting migration of allocation_classification attribute");
        List<MigratorHistory> migratorHistories =
                repository.findByMigratorName(MigratorName.ALLOCATION_CLASSIFICATION_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistories)) {
            log.info("[Allocation Classification Migrator], has already performed previously, skipping.");
            return;
        }
        List<Account> activeAccountsWithCompliantEntities =
                accountRepository.findOHA_AOHAAccountsWithCompliantEntities();
        if (activeAccountsWithCompliantEntities == null) {
            log.debug("Active Accounts With Compliant Entities not found!");
            return;
        }
        activeAccountsWithCompliantEntities.forEach(account -> {
            CompliantEntity compliantEntity = account.getCompliantEntity();
            compliantEntity.setAllocationClassification(
                    allocationTableService.updateAllocationClassification(compliantEntity.getId()));
            compliantEntityRepository.save(compliantEntity);
        });

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ALLOCATION_CLASSIFICATION_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        repository.save(migratorHistory);
        log.info("Migration of allocation classification completed");
    }
}
