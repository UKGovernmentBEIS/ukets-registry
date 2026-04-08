package gov.uk.ets.registry.api.common.reporting.metrics.migration;


import gov.uk.ets.registry.api.user.migration.SchedulerHelper;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!integrationTest")
public class AccountReportingMetricsMigratorInitiator implements CommandLineRunner {

    private static final String SCHED_LOCK_NAME = "AccountReportingMetricsMigratorInitiator";

    private final AccountReportingMetricsMigrator migrator;
    private final LockProvider lockProvider;

    @Autowired
    private SchedulerHelper helper;

    @Override
    public void run(String... args) throws Exception {
    	
        LockConfiguration lockConfiguration = helper.createLockConfiguration(SCHED_LOCK_NAME);
        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
        
        try {
            executor.executeWithLock(migrator, lockConfiguration);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
    
}
