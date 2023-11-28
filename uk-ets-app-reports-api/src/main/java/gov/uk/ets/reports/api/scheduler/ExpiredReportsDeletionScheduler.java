package gov.uk.ets.reports.api.scheduler;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.reports.api.ReportService;
import gov.uk.ets.reports.api.domain.Report;
import gov.uk.ets.reports.model.ReportStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExpiredReportsDeletionScheduler {

    @Value("${aws.s3.reports.bucket.name}")
    private String bucketName;

    private final S3ClientService s3ClientService;

    private final ReportService reportService;

    /**
     * Retrieves expired reports and for each one
     * deletes the S3 file and the entry in the reports table.
     */
    @Scheduled(cron = "${scheduler.reports.deletion.start}")
    @SchedulerLock(name = "reportsSchedulerLock", lockAtLeastFor = "500ms")
    public void execute() {
        // To assert that the lock is held (prevents misconfiguration errors)
        LockAssert.assertLocked();
        log.info("Scheduled deletion of reports started");

        List<Report> expiredPendingReports = reportService.retrievePendingReportsToExpire();

        List<Report> expiredReports = reportService.retrieveExpiredReports();

        expiredReports.addAll(expiredPendingReports);

        expiredReports.forEach(report -> {
            if (report.getStatus() == ReportStatus.DONE) {
                s3ClientService.deleteFile(bucketName, report.getFileName());
            }
            reportService.deleteReport(report);
            log.debug("Report with id: {} deleted", report.getId());
        });

        log.info("Scheduled deletion of reports ended");
    }
}
