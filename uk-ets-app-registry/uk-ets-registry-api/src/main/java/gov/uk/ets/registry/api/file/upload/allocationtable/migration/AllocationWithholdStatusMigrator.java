package gov.uk.ets.registry.api.file.upload.allocationtable.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class AllocationWithholdStatusMigrator implements Migrator {

    private final MigratorHistoryRepository repository;
    private final AccountAllocationService accountAllocationService;
    private final CompliantEntityRepository compliantEntityRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void migrate() {
        log.info("Starting migration of ALLOWED/WITHHELD values for allocation_withhold_status attribute");
        List<MigratorHistory> migratorHistories = 
                repository.findByMigratorName(MigratorName.ALLOCATION_WITHHOLD_STATUS_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistories)) {
            log.info("[Migration of ALLOWED/WITHHELD values for allocation_withhold_status], " +
                    "has already performed, skipping.");
            return;
        }
        List<Account> activeAccountsWithCompliantEntities = 
                accountRepository.findOHA_AOHAAccountsWithCompliantEntities();
        if (activeAccountsWithCompliantEntities == null) {
            log.debug("Active Accounts With Compliant Entities not found");
            return;
        }
        activeAccountsWithCompliantEntities.forEach(account -> {
            CompliantEntity compliantEntity = account.getCompliantEntity();
            Map<Integer, AllocationStatusType> allocationStatuses = 
                    accountAllocationService.getAccountAllocationStatus(account.getIdentifier());
            if (!allocationStatuses.isEmpty() && allocationStatuses
                    .values()
                    .stream()
                    .noneMatch(AllocationStatusType.WITHHELD::equals)) {
                compliantEntity.setAllocationWithholdStatus(AllocationStatusType.ALLOWED);
            } else if (!allocationStatuses.isEmpty() && allocationStatuses
                    .values()
                    .stream()
                    .anyMatch(AllocationStatusType.WITHHELD::equals)) {
                compliantEntity.setAllocationWithholdStatus(AllocationStatusType.WITHHELD);
            }
            compliantEntityRepository.save(compliantEntity);
        });
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ALLOCATION_WITHHOLD_STATUS_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        repository.save(migratorHistory);
        log.info("Migration completed");
    }

}
