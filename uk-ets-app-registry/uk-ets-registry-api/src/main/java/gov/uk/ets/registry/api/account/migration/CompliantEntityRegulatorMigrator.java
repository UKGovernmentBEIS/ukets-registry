package gov.uk.ets.registry.api.account.migration;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
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
public class CompliantEntityRegulatorMigrator implements Migrator {

    private final CompliantEntityRepository compliantEntityRepository;
    private final TaskRepository taskRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    @Override
    public void migrate() {
        log.info("Starting Compliant Entity Regulator Migrator");

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.COMPLIANT_ENTITY_REGULATOR_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of compliant entity regulator already performed previously, skipping.");
            return;
        }

        List<CompliantEntity> compliantEntities = compliantEntityRepository.findByRegulator(RegulatorType.BEIS_OPRED);
        compliantEntities.forEach(c -> {
            c.setRegulator(RegulatorType.OPRED);
            compliantEntityRepository.save(c);
        });

        compliantEntities = compliantEntityRepository.findByChangedRegulator(RegulatorType.BEIS_OPRED);
        compliantEntities.forEach(c -> {
            c.setChangedRegulator(RegulatorType.OPRED);
            compliantEntityRepository.save(c);
        });

        final String BEIS_OPRED_REGULATOR_REGEX = "\"regulator\":\""+RegulatorType.BEIS_OPRED.name()+"\"";
        final String BEIS_OPRED_CHANGED_REGULATOR_REGEX = "\"changedRegulator\":\""+RegulatorType.BEIS_OPRED.name()+"\"";
        final String OPRED_REGULATOR_REGEX = "\"regulator\":\""+RegulatorType.OPRED.name()+"\"";
        final String OPRED_CHANGED_REGULATOR_REGEX = "\"changedRegulator\":\""+RegulatorType.OPRED.name()+"\"";

        QTask task = QTask.task;
        Iterable<Task> tasksWithRegulator = taskRepository.findAll(
                task.difference.contains(BEIS_OPRED_CHANGED_REGULATOR_REGEX)
                        .or(task.difference.contains(BEIS_OPRED_REGULATOR_REGEX))
                        .or(task.before.contains(BEIS_OPRED_REGULATOR_REGEX)));
        for (Task t : tasksWithRegulator) {
            if(t.getDifference() != null){
                t.setDifference(t.getDifference().replaceAll(BEIS_OPRED_REGULATOR_REGEX, OPRED_REGULATOR_REGEX));
                t.setDifference(t.getDifference().replaceAll(BEIS_OPRED_CHANGED_REGULATOR_REGEX, OPRED_CHANGED_REGULATOR_REGEX));
            }
            if(t.getBefore() != null) {
                t.setBefore(t.getBefore().replaceAll(BEIS_OPRED_REGULATOR_REGEX, OPRED_REGULATOR_REGEX));
            }
            taskRepository.save(t);
        }

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.COMPLIANT_ENTITY_REGULATOR_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);

        log.info("Migration of compliant entity regulator completed");
    }

}
