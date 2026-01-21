package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ChangeYearEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.sis.internal.util.StandardDateFormat;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a scheduled job that runs at the start of every year and sends a
 * compliance ChangeYearEvent for every active account in the registry.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Profile("!integrationTest")
public class ChangeYearEventScheduler {

    private final ComplianceEventService service;
    private final AccountRepository accountRepository;

    @SchedulerLock(name = "changeYearEventSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.change.year.start}")
    @Transactional
    public void execute() {
        LockAssert.assertLocked();

        List<Account> activeAccounts = accountRepository
                .findActiveAccountsWithCompliantEntities();

        activeAccounts.forEach(account -> {
            // Initially, at this point the excluded year were rolled over.
            // According to UKETS-8552 this is no longer needed.

            log.info(
                    "Creating compliance change year event for account: {}",
                    account.getIdentifier());
            LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
            service.processEvent(ChangeYearEvent.builder().actorId("system")
                    .dateTriggered(now)
                    .dateRequested(now)
                    .compliantEntityId(
                            account.getCompliantEntity().getIdentifier())
                    .build());
        });
    }
}
