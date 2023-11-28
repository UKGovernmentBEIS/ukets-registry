package gov.uk.ets.registry.api.reconciliation;

import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for initiating reconciliations.
 */
@Component
@Log4j2
@AllArgsConstructor
public class ReconciliationScheduler {

    /**
     * Service for performing reconciliations
     */
    private ProcessReconciliationService reconciliationService;

    /**
     * Initiates a reconciliation at a scheduled time.
     */
    @SchedulerLock(name = "reconciliationSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.reconciliation.start:0 30 0 * * *}")
    public void execute() {
        Date startDate = new Date();
        LockAssert.assertLocked();
        reconciliationService.initiate(startDate);
    }

}
