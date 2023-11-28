package gov.uk.ets.registry.api.account.migration;

import gov.uk.ets.registry.api.migration.ScheduledMigrator;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountOpeningTaskMigratorInitializer implements CommandLineRunner, ScheduledMigrator {

    private static final String SCHED_LOCK_NAME = "AccountOpeningTaskMigratorSchedulerLock";

    private final LockProvider lockProvider;
    private final TaskScheduler taskScheduler;
    private final AccountOpeningTaskMigrator migrator;

    @Override
    public void run(String... args) throws Exception {
        schedule(lockProvider,taskScheduler,migrator);
    }

    @Override
    public String getSchedulerLockName() {
        return SCHED_LOCK_NAME;
    }

}