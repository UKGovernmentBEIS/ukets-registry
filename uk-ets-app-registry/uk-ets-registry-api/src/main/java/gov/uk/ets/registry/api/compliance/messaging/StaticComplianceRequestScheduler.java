package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.StaticComplianceRequestEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@Profile("!integrationTest")
public class StaticComplianceRequestScheduler {

    /**
     * Parses an ISO LocalDateTime string <strong>without</strong> the year (using current year instead).
     */
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .parseDefaulting(ChronoField.YEAR, LocalDateTime.now().getYear())
        .appendPattern("MM-dd'T'HH:mm:ss")
        .toFormatter();

    private final ComplianceEventService service;

    private final AccountRepository accountRepository;

    /**
     * This is the date/time before which we want to retrieve the static compliance state.
     */
    private final LocalDateTime cutoffDate;

    public StaticComplianceRequestScheduler(
        ComplianceEventService service,
        AccountRepository accountRepository,
        @Value("${scheduler.static.compliance.cutoff.date}")
            String cutoffDate
    ) {
        this.service = service;
        this.accountRepository = accountRepository;
        this.cutoffDate = LocalDateTime.parse(cutoffDate, formatter);
    }


    @SchedulerLock(name = "staticComplianceRequestSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.static.compliance.request.start}")
    @Transactional
    public void execute() {
        LockAssert.assertLocked();

        if (cutoffDate == null) {
            throw new IllegalStateException("The property 'scheduler.static.compliance.cutoff.date' was not found");
        }

        LocalDateTime dateTriggered = LocalDateTime.now(ZoneId.of("UTC"));

        List<Account> accounts = accountRepository.findOHA_AOHAAccountsWithCompliantEntities();
        accounts
            .stream()
            .filter(account -> !closedForAtLeastTwoYears(account, dateTriggered.getYear()))
            .map(Account::getCompliantEntity)
            .filter(compliantEntity -> !service.skipStaticComplianceStatusRequestForEntity(compliantEntity,
                dateTriggered.getYear()))
            .forEach(compliantEntity -> {

                log.info("Creating static compliance request event for compliant entity: {}",
                    compliantEntity.getIdentifier());

                service.processEvent(StaticComplianceRequestEvent.builder()
                    .actorId("system")
                    .dateTriggered(dateTriggered)
                    .dateRequested(cutoffDate)
                    .compliantEntityId(compliantEntity.getIdentifier())
                    .build());
            });
    }

    private boolean closedForAtLeastTwoYears(Account account, int currentYear) {
        return Optional.ofNullable(account.getClosingDate())
                       .map(d -> LocalDateTime.ofInstant(d.toInstant(), ZoneId.of("UTC")))
                       .map(LocalDateTime::getYear)
                       .filter(yearOfClosure -> currentYear > yearOfClosure + 1)
                       .isPresent();
    }
}
