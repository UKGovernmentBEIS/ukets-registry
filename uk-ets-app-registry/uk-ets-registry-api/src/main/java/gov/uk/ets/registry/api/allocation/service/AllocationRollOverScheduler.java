package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a scheduled job that runs at the start of every year and
 * handles the roll-over for the withheld status.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Profile("!integrationTest")
public class AllocationRollOverScheduler {

    private final AccountRepository accountRepository;
    private final AllocationStatusService allocationStatusService;

    @SchedulerLock(name = "allocationRollOverSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.allocation.rollover}")
    @Transactional
    public void execute() {
        LockAssert.assertLocked();

        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;

        accountRepository.findActiveAccountsWithCompliantEntities()
            .stream()
            .map(Account::getCompliantEntity)
            .filter(compliantEntity -> shouldRollOver(compliantEntity, lastYear))
            .forEach(compliantEntity -> rollOverWithheldAllocationStatus(compliantEntity, currentYear, lastYear));
    }

    private void rollOverWithheldAllocationStatus(CompliantEntity compliantEntity, int currentYear, int lastYear) {

        Set<Integer> yearsOfInterest = Stream.of(currentYear, lastYear).collect(Collectors.toSet());

        Map<Integer, AllocationStatusType> allocationStatusMap =
            allocationStatusService.getAllocationStatus(compliantEntity.getId())
                .stream()
                .filter(allocationSummary -> yearsOfInterest.contains(allocationSummary.getYear()))
                .collect(Collectors.toMap(AllocationSummary::getYear, AllocationSummary::getStatus));

        AllocationStatusType previousStatus = allocationStatusMap.get(lastYear);
        if (previousStatus != AllocationStatusType.WITHHELD) {
            return;
        }

        if (allocationStatusMap.containsKey(currentYear)) {
            allocationStatusService.updateAllocationStatus(compliantEntity.getId(), Map.of(currentYear, previousStatus));
        } else {
            log.warn("Unable to roll over allocation status for CompliantEntity with identifier {}", compliantEntity.getIdentifier());
        }
    }

    private boolean shouldRollOver(CompliantEntity compliantEntity, long lastYear) {
        return isAfterStartYear(compliantEntity, lastYear) && isBeforeEndYear(compliantEntity, lastYear);
    }

    private boolean isAfterStartYear(CompliantEntity compliantEntity, long lastYear) {
        return Optional.ofNullable(compliantEntity.getStartYear()).filter(start -> start <= lastYear).isPresent();
    }

    private boolean isBeforeEndYear(CompliantEntity compliantEntity, long lastYear) {
        return Optional.ofNullable(compliantEntity.getEndYear()).filter(end -> end <= lastYear).isEmpty();
    }
}
