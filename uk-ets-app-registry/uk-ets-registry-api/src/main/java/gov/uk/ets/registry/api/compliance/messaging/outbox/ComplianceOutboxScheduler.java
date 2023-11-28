package gov.uk.ets.registry.api.compliance.messaging.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ComplianceOutboxScheduler {

    private final ComplianceOutboxService service;

    @Scheduled(cron = "${scheduler.compliance.outbox.process}")
    @SchedulerLock(name = "complianceOutboxSchedulerLock")
    public void processOutboxEvents() {
        log.debug("starting processing of compliance outbox events...");
        service.processEvents();
        log.debug("Processing of compliance outbox ended");
    }
}
