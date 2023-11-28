package gov.uk.ets.reports.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.reports.api.domain.Report;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = "spring.test.database.replace=NONE")
class ReportRepositoryIntegrationTest extends BasePostgresFixture {

    private static final String TEST_USER_1 = "UK1234567890";

    LocalDateTime requestDate1 = LocalDateTime.of(2021, 1, 1, 22, 22, 22, 123456);
    LocalDateTime requestDate2 = LocalDateTime.of(2021, 1, 1, 22, 22, 23, 123456);
    LocalDateTime requestDate3 = LocalDateTime.of(2021, 1, 1, 22, 22, 21, 123456);

    @Autowired
    private ReportRepository reportRepository;

    @Test
    public void shouldSaveNewReport() {
        LocalDateTime now = LocalDateTime.now();
        Report report = Report.builder()
            .type(ReportType.R0001)
            .status(ReportStatus.PENDING)
            .requestDate(now)
            .requestingUser(TEST_USER_1)
            .build();
        Report saved = reportRepository.save(report);

        // make sure the record is saved in database correctly
        flushAndClear();

        Report retrieved =
            reportRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalStateException("Report not found"));

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getType()).isEqualTo(ReportType.R0001);
        assertThat(retrieved.getRequestingUser()).isEqualTo(TEST_USER_1);
        assertThat(retrieved.getRequestDate()).isEqualToIgnoringNanos(now);
    }

    @Test
    public void shouldThrowWhenSavingReportWithoutStatus() {
        LocalDateTime now = LocalDateTime.now();
        Report report = Report.builder()
            .type(ReportType.R0001)
            .requestDate(now)
            .requestingUser(TEST_USER_1)
            .build();
        reportRepository.save(report);

        // flushing after saving throws an exception because of the NOT NULL constraint in database
        assertThrows(PersistenceException.class, this::flushAndClear);

    }

    @Test
    public void shouldRetrieveExpiredReportsOnly() {

        createSampleReports();

        List<Report> expiredReports =
            reportRepository.findByGenerationDateBeforeAndStatusNot(requestDate2, ReportStatus.PENDING);

        assertThat(expiredReports).hasSize(2);

        assertThat(expiredReports).extracting(Report::getType).containsOnly(ReportType.R0001, ReportType.R0003);
    }

    @Test
    public void shouldRetrieveExpiredPendingReportsOnly() {
        createSampleReports();

        List<Report> expiredPendingReports =
            reportRepository.findByRequestDateBeforeAndStatus(requestDate2, ReportStatus.PENDING);

        assertThat(expiredPendingReports).hasSize(1);

        assertThat(expiredPendingReports).extracting(Report::getType).containsOnly(ReportType.R0004);
    }

    private void createSampleReports() {

        Report expiredReport = Report.builder()
            .type(ReportType.R0001)
            .status(ReportStatus.DONE)
            .requestDate(requestDate1)
            .generationDate(requestDate1)
            .requestingUser(TEST_USER_1)
            .build();

        Report nonExpiredReport = Report.builder()
            .type(ReportType.R0002)
            .status(ReportStatus.DONE)
            .requestDate(requestDate2)
            .generationDate(requestDate2)
            .requestingUser(TEST_USER_1)
            .build();

        Report expiredFailedReport = Report.builder()
            .type(ReportType.R0003)
            .status(ReportStatus.FAILED)
            .requestDate(requestDate3)
            .generationDate(requestDate3)
            .requestingUser(TEST_USER_1)
            .build();

        Report expiredPendingReport = Report.builder()
            .type(ReportType.R0004)
            .status(ReportStatus.PENDING)
            .requestDate(requestDate1)
            .generationDate(requestDate1)
            .requestingUser(TEST_USER_1)
            .build();

        reportRepository.saveAll(List.of(expiredReport, nonExpiredReport, expiredFailedReport, expiredPendingReport));

        flushAndClear();
    }


}
