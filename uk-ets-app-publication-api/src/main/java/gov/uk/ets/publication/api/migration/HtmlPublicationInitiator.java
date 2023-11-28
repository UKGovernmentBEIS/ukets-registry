package gov.uk.ets.publication.api.migration;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Profile("!integrationTest")
public class HtmlPublicationInitiator implements CommandLineRunner {

    private static final String SCHED_LOCK_NAME = "HtmlPublicationSchedulerLock";
  
    private final HtmlPublicationMigrator migrator;
    private final SchedulerHelper helper;


    @Override
    public void run(String... args) {
        LockableTaskScheduler lockableTaskScheduler = helper.createScheduler(SCHED_LOCK_NAME);
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }
}
