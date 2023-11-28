package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ChangeYearEvent;
import gov.uk.ets.registry.api.compliance.messaging.exception.IllegalYearForRollOverException;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final EmissionsEntryRepository emissionsEntryRepository;
    private final ExcludeEmissionsRepository excludeEmissionsRepository;
    private final EventService eventService;

    @SchedulerLock(name = "changeYearEventSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.change.year.start}")
    @Transactional
    public void execute() {
        LockAssert.assertLocked();

        List<Account> activeAccounts = accountRepository
                .findActiveAccountsWithCompliantEntities();

        activeAccounts.forEach(account -> {
            try {
                rollOverExcludedYear(account.getCompliantEntity());

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
            } catch (IllegalYearForRollOverException e) {
                eventService.createAndPublishEvent(
                        Long.toString(e.getCompliantEntityIdentifier()), null,
                        String.format("Year: %d, Emissions uploaded: %d",
                                e.getLastYear(), e.getEmissions()),
                        EventType.COMPLIANT_ENTITY_ROLL_OVER_FAILURE,
                        "Exclusion status roll-over failed due to already uploaded emissions");
            }
        });

    }

    /**
     * <ol>
     * <li>Excluded status gets rolled over to the current year. More specifically, on 1/1/XXXX, 
     * when Year Exclusion status roll-over takes place, it sets the exclusion status of year XXXX 
     * to the same value as for year XXXX-1. i.e. on 1/1/2024 if exclusion status for 2023 was TRUE, 
     * the exclusion status for year 2024 will be set to TRUE as well</li>
     * </ol>
     * 
     * @param compliantEntityIdentifier
     */
    private void rollOverExcludedYear(CompliantEntity compliantEntity) {
        long currentYear = LocalDateTime.now().getYear();
        long lastYear = currentYear - 1L;
        
        if (shouldSkipRollOverOfExclusionYear(compliantEntity,lastYear)) {
            return;
        }

        // Check if there are verified emissions uploaded for the currentYear
        List<EmissionsEntry> emissions = emissionsEntryRepository
                .findAllByCompliantEntityIdAndYear(
                        compliantEntity.getIdentifier(), currentYear)
                .stream()
                .sorted(Comparator.comparing(EmissionsEntry::getUploadDate))
                .collect(Collectors.toList());
        if (!emissions.isEmpty()) {
            throw new IllegalYearForRollOverException(
                    compliantEntity.getIdentifier(), currentYear,
                    emissions.get(0).getEmissions());
        }
        
        Optional<ExcludeEmissionsEntry> excludedEntryLastYearOptional = excludeEmissionsRepository
                .findByCompliantEntityId(compliantEntity.getIdentifier())
                .stream().filter(e -> e.getYear() == lastYear).findAny();

        if (excludedEntryLastYearOptional.isPresent()
                && excludedEntryLastYearOptional.get().isExcluded()) {
            
            Optional<ExcludeEmissionsEntry> excludedEntryCurrentYearOptional = excludeEmissionsRepository
                    .findByCompliantEntityId(compliantEntity.getIdentifier())
                    .stream().filter(e -> e.getYear() == currentYear).findAny();

            if (excludedEntryCurrentYearOptional.isPresent()
                    && !excludedEntryCurrentYearOptional.get().isExcluded()) {
                excludedEntryCurrentYearOptional.get().setExcluded(true);
                excludedEntryCurrentYearOptional.get().setLastUpdated(new Date());
                excludeEmissionsRepository
                        .save(excludedEntryCurrentYearOptional.get());
            } else {
                ExcludeEmissionsEntry excludedEntryCurrentYear = new ExcludeEmissionsEntry();
                excludedEntryCurrentYear.setYear(currentYear);
                excludedEntryCurrentYear
                        .setCompliantEntityId(compliantEntity.getIdentifier());
                excludedEntryCurrentYear.setExcluded(true);
                excludedEntryCurrentYear.setLastUpdated(new Date());
                excludeEmissionsRepository.save(excludedEntryCurrentYear);
            }
        }
    }
    
    /**
     * 
     * @param compliantEntity
     * @param lastYear
     * @return true if the roll over <strong>should not</strong> be performed
     *         false otherwise
     */
    private boolean shouldSkipRollOverOfExclusionYear(
            CompliantEntity compliantEntity, long lastYear) {
        // We must be at least 1 years ahead of start before a roll over takes
        // place.
        if (Optional.ofNullable(compliantEntity.getStartYear())
                .orElseThrow() > lastYear) {
            return true;
        }

        // We must be at most on the same year as LYVE for a roll over to take
        // place.
        Optional<Integer> endYearOptional = Optional
                .ofNullable(compliantEntity.getEndYear());
        if (endYearOptional.isPresent()
                && endYearOptional.get() <= lastYear) {
            return true;
        }

        return false;
    }
}
