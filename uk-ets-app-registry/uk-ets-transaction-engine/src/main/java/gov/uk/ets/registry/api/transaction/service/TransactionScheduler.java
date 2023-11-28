package gov.uk.ets.registry.api.transaction.service;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduler for transactions.
 */
@Service
public class TransactionScheduler {

    private final TransactionStarter transactionStarter;
    private final int maxPeriodInHours;

    public TransactionScheduler(TransactionStarter transactionStarter, @Value("${transaction.processing.max.period.in.hours}") Integer maxPeriodInHours){
        this.transactionStarter = transactionStarter;
        this.maxPeriodInHours = maxPeriodInHours;
    }


    /**
     * Launches delayed transactions which are eligible to start.
     */
    @SchedulerLock(name = "transactionSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.transaction.start}")
    public void launchDelayedTransactions() {
        LockAssert.assertLocked();
        transactionStarter.startDelayedTransactions();
        transactionStarter.checkForStoppedTransactions(maxPeriodInHours);
    }

}
