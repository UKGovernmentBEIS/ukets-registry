package gov.uk.ets.registry.api.account.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockManager;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!integrationTest")
public class AccountsComplianceMigration implements CommandLineRunner {

    private static final int LOCK_AT_MOST_SECONDS = 60;
    private static final int LOCK_AT_LEAST_SECONDS = 2;
    private static final String SCHED_LOCK_NAME = "AccountsComplianceMigratorSchedulerLock";

    private final LockProvider lockProvider;
    private final TaskScheduler taskScheduler;
    private final AccountsComplianceMigrator migrator;

    @Override
    public void run(String... args) throws Exception {
        LockableTaskScheduler lockableTaskScheduler = createScheduler();
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }

    private LockableTaskScheduler createScheduler() {
        LockConfiguration lockConfiguration =
            new LockConfiguration(Instant.now(), SCHED_LOCK_NAME, Duration.ofSeconds(LOCK_AT_MOST_SECONDS),
                Duration.ofSeconds(LOCK_AT_LEAST_SECONDS));
        LockConfigurationExtractor lockConfigurationExtractor = (Runnable task) -> Optional.of(lockConfiguration);
        LockManager lockManager = new DefaultLockManager(lockProvider, lockConfigurationExtractor);
        return new LockableTaskScheduler(taskScheduler, lockManager);
    }
}
