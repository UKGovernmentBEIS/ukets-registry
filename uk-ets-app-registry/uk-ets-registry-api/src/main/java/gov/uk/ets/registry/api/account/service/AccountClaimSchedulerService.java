package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.web.model.BulkClaimResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountClaimSchedulerService {

    private final TaskScheduler taskScheduler;
    private final AccountClaimService accountClaimService;


    public void scheduleBulkClaim() {

        Instant runAt = Instant.now().plusSeconds(3);

        taskScheduler.schedule(this::runBulkClaim, runAt);
    }

    @SchedulerLock(
            name = "BulkClaimSchedulerLock",
            lockAtMostFor = "30m",
            lockAtLeastFor = "1m"
    )
    public void runBulkClaim() {

        log.info("Bulk account claim STARTED");
        long start = System.currentTimeMillis();

        try {
            BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

            long duration = System.currentTimeMillis() - start;

            log.info(
                    "Bulk account claim FINISHED total={} success={} failed={} duration={}ms",
                    result.getTotal(),
                    result.getSuccessful(),
                    result.getFailed(),
                    duration
            );

        } catch (Exception ex) {
            log.error("Bulk account claim FAILED completely", ex);
        }
    }
}
