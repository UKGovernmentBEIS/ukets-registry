package gov.uk.ets.registry.api.file.upload.migration;

import com.querydsl.jpa.JPAExpressions;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
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
public class ParentTaskDeadlineMigrator implements Migrator {

    private final TaskRepository taskRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    @Override
    public void migrate() {
        log.info("Starting Parent Task Deadline Migrator");
        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(MigratorName.PARENT_TASK_DEADLINE_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of Parent Task Deadline already performed previously, skipping.");
            return;
        }

        QTask task = QTask.task;
        Iterable<Task> parentTasks = taskRepository.findAll(task.id.in(
            JPAExpressions.select(task.parentTask.id)
                .from(task)
                .where(task.parentTask.isNotNull()
                    .and(task.parentTask.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)))));

        for (Task parent : parentTasks) {
            taskRepository.findLatestSubTaskDeadline(parent.getId()).ifPresent(parent::setDeadline);
        }

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.PARENT_TASK_DEADLINE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of Parent Task Deadline completed");
    }
}
