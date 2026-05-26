package gov.uk.ets.registry.api.accounttransfer.migration;

import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.shared.AccountTransferDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountTransferEmitterIdMigrator implements net.javacrumbs.shedlock.core.LockingTaskExecutor.Task {

    private final TaskRepository taskRepository;
    private final Mapper mapper;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Override
    @Transactional
    public void call() throws Throwable {
        log.info("AccountTransferEmitterIdMigrator");
         
        List<MigratorHistory> migratorHistoryList = migratorHistoryRepository
             .findByMigratorName(MigratorName.ACCOUNT_TRANSFER_EMITTER_ID_MIGRATOR);
         
        if (!migratorHistoryList.isEmpty()) {
            log.info("[AccountTransferEmitterIdMigrator] has already been performed, skipping.");
            return;
        }
         
        QTask task = QTask.task;
        Iterable<Task> transferTasks = taskRepository.findAll(
             task.type.eq(RequestType.ACCOUNT_TRANSFER).and(task.before.isNotNull().or(task.before.isNotEmpty())),
             Sort.by(Sort.Direction.ASC, "initiatedDate"));
         
        for (Task transferTask : transferTasks) {
         
            AccountHolderDTO accountHolderDTO = mapper.convertToPojo(transferTask.getBefore(), AccountHolderDTO.class);
            AccountTransferDTO accountTransferDto = new AccountTransferDTO(accountHolderDTO, null);
            transferTask.setBefore(mapper.convertToJson(accountTransferDto));
            taskRepository.save(transferTask);
        }
        
        updateMigrationHistory();
        log.info("[AccountTransferEmitterIdMigrator] completed for {} tasks.");
    }
    
    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_TRANSFER_EMITTER_ID_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }

}
