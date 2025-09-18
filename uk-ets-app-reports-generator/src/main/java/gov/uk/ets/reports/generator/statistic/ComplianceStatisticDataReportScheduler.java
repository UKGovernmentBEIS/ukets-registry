package gov.uk.ets.reports.generator.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ComplianceStatisticDataReportScheduler {

    private final ComplianceStatisticDataReportService service;

    @Scheduled(cron = "${scheduler.statistic.compliance.report.start}")
    @Transactional
    public void execute() {
        service.calculateSnapshot();
    }
}
