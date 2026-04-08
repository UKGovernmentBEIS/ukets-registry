package gov.uk.ets.registry.api.common.reporting.metrics.outbox;

import gov.uk.ets.registry.api.common.reporting.metrics.service.ReportingMetricsOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReportingMetricsOutboxScheduler {

    private final ReportingMetricsOutboxService reportingMetricsOutboxService;

    @Scheduled(cron = "${scheduler.reporting.metrics.outbox.process:0/5 * * * * *}")
    @SchedulerLock(name = "reportingMetricsOutboxSchedulerLock")
    public void processOutboxEvents() {
        log.debug("starting processing of reporting metrics outbox events...");
        reportingMetricsOutboxService.processEvents();
        log.debug("Processing of reporting metrics outbox ended");
    }
}
