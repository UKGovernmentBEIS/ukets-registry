package gov.uk.ets.registry.api.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This scheduler periodically check for expired used links and removes them.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class UsedTokenScheduler {

    private final UsedTokenService usedTokenService;

    @SchedulerLock(name = "usedTokenServiceLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.expired.used.links.cleanup:0 0 4 * * *}")
    public void removeUsedTokens() {

        log.info("Delete expired used links.");

        usedTokenService.deleteExpiredTokens();
    }
}
