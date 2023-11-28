package gov.uk.ets.registry.api.user.migration;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Profile("!integrationTest")
public class UserEmailMigratorInitiator implements CommandLineRunner {


    private static final String SCHED_LOCK_NAME = "UserEmailMigratorSchedulerLock";

    private final UserEmailMigrator migrator;

    @Autowired
    private SchedulerHelper helper;

    @Override
    public void run(String... args) throws Exception {
        LockableTaskScheduler lockableTaskScheduler = helper.createScheduler(SCHED_LOCK_NAME);
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }
}
