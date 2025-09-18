package gov.uk.ets.registry.api.file.upload.migration;

import gov.uk.ets.registry.api.migration.ScheduledMigrator;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentTaskDeadlineMigratorInitializer implements CommandLineRunner, ScheduledMigrator {

    private static final String SCHED_LOCK_NAME = "ParentTaskDeadlineMigratorSchedulerLock";

    private final LockProvider lockProvider;
    private final TaskScheduler taskScheduler;
    private final ParentTaskDeadlineMigrator migrator;

    @Override
    public void run(String... args) throws Exception {
        schedule(lockProvider,taskScheduler,migrator);
    }

    @Override
    public String getSchedulerLockName() {
        return SCHED_LOCK_NAME;
    }

}