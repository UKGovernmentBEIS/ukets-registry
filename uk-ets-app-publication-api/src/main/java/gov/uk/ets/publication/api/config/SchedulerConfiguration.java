package gov.uk.ets.publication.api.config;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M", defaultLockAtLeastFor = "PT3S")
@RequiredArgsConstructor
public class SchedulerConfiguration {

    private final JdbcTemplate jdbcTemplate;

    /**
     * By specifying usingDbTime() the lock provider will use UTC time based on the DB server time.
     * If it is not specified, current time on the client will be used (the time may differ between clients).
     */
    @Bean
    public LockProvider lockProvider() {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(jdbcTemplate)
                .usingDbTime()
                .build()
        );
    }
}