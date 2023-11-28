package gov.uk.ets.registry.api.tal.migration;

import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountTaskDifference;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class TrustedAccountStatusMigrator implements Migrator {

    private final MigratorHistoryRepository migratorHistoryRepository;
    private final TrustedAccountRepository trustedAccountRepository;
    private final TaskRepository taskRepository;
    private final Mapper mapper;

    @Transactional
    @Override
    public void migrate() {

        log.info("Starting Trusted Account Status Migrator");
        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(MigratorName.TRUSTED_ACCOUNT_STATUS);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of Trusted Account Status already performed previously, skipping.");
            return;
        }

        QTask task = QTask.task;
        Iterable<Task> pendingTrustedAccountRemovals = taskRepository.findAll(
            task.type.eq(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST)
                .and(task.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)));

        for (Task pendingTrustedAccountRemoval : pendingTrustedAccountRemovals) {
            TrustedAccountTaskDifference trustedAccountTaskDifference =
                mapper.convertToPojo(pendingTrustedAccountRemoval.getDifference(), TrustedAccountTaskDifference.class);

            List<TrustedAccount> trustedAccounts = trustedAccountRepository.findByIdIn(trustedAccountTaskDifference.getIds());

            trustedAccounts.stream()
                .filter(trustedAccount -> trustedAccount.getStatus() == TrustedAccountStatus.ACTIVE)
                .forEach(trustedAccount -> trustedAccount.setStatus(TrustedAccountStatus.PENDING_REMOVAL_APPROVAL));
        }


        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.TRUSTED_ACCOUNT_STATUS);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of Trusted Account Status completed");
    }
}
