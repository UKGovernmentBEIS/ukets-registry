package gov.uk.ets.registry.api.compliance.migration;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class StaticComplianceStatusMigrator implements Migrator {

    private final CompliantEntityRepository compliantEntityRepository;
    private final StaticComplianceStatusRepository staticComplianceStatusRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    @Override
    public void migrate() {
        log.info("Starting Static Compliance Status Migrator");

        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(MigratorName.STATIC_COMPLIANCE_STATUS_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of Starting Static Compliance Status already performed previously, skipping.");
            return;
        }

        List<CompliantEntity> compliantEntitiesWithCompletedPeriod =
            compliantEntityRepository.findByEndYearBefore(LocalDate.now().getYear());

        compliantEntitiesWithCompletedPeriod.forEach(compliantEntity -> {
            List<StaticComplianceStatus> lastStatuses =
                staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(
                    compliantEntity.getIdentifier(),
                    (long) compliantEntity.getEndYear());

            List<StaticComplianceStatus> toBeDeleted =
                lastStatuses.stream()
                    .filter(complianceStatus -> complianceStatus.getComplianceStatus() == ComplianceStatus.A
                        || complianceStatus.getComplianceStatus() == ComplianceStatus.EXCLUDED)
                    .sorted(Comparator.comparing(StaticComplianceStatus::getYear))
                    .skip(1)
                    .toList();

            staticComplianceStatusRepository.deleteAll(toBeDeleted);
        });

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.STATIC_COMPLIANCE_STATUS_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);

        log.info("Migration of compliant entity regulator completed");
    }

}
