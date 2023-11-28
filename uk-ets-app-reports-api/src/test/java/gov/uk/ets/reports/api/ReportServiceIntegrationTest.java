package gov.uk.ets.reports.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.reports.api.domain.Report;
import gov.uk.ets.reports.api.error.UkEtsReportsException;
import gov.uk.ets.reports.api.web.model.ReportCreationRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationResponse;
import gov.uk.ets.reports.api.web.model.ReportDto;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.jdbc.JdbcTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;


/**
 * For the moment S3 client and kafka template are mocked, only the integration with the db is tested here.
 */
@SpringBootTest
class ReportServiceIntegrationTest extends BasePostgresFixture {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String TEST_URID_1 = "UK1234567890";
    private static final String TEST_URID_2 = "UK1234567891";
    private static final String TEST_URID_3 = "UK1234567892";
    private static final String TEST_URID_4 = "UK1234567893";
    private static final String TEST_URID_5 = "UK1234567894";

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @MockBean
    S3ClientService s3ClientService;

    @MockBean
    KafkaTemplate<String, Serializable> reportsApiKafkaTemplate;

    @BeforeEach
    public void setUp() throws IOException, ExecutionException, InterruptedException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "report");

        when(s3ClientService.downloadFile(anyString(), anyString()))
            .thenReturn(
                new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    AbortableInputStream.create(IOUtils.toInputStream("test", "UTF-8"))
                )
            );

        when(reportsApiKafkaTemplate.send(any(), any())).thenReturn(new AsyncResult<>(null));
    }

    @Test
    public void shouldSaveNewReportRequest() {
        ReportCreationRequest creationRequest = ReportCreationRequest.builder()
            .type(ReportType.R0001)
            .queryInfo(new ReportQueryInfo())
            .build();

        ReportCreationResponse reportCreationResponse =
            reportService.requestReport(creationRequest, TEST_URID_1);

        assertThat(reportCreationResponse.getReportId()).isNotNull();

        int reportCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "report");
        assertThat(reportCount).isEqualTo(1);
    }

    @Test
    public void shouldFailSavingReportWithNullUrid() {
        ReportCreationRequest creationRequest = ReportCreationRequest.builder()
            .type(ReportType.R0001)
            .queryInfo(new ReportQueryInfo())
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> reportService.requestReport(creationRequest, null));
    }

    @Test
    public void shouldListAllReportsForAdmin() {

        LocalDateTime now1 = LocalDateTime.now();
        LocalDateTime now2 = LocalDateTime.now().plusSeconds(5L);
        Report report1 = generateTestReport(now1, null, TEST_URID_1, ReportStatus.PENDING, true);
        Report report2 = generateTestReport(now2, null, TEST_URID_2, ReportStatus.DONE, true);

        List<ReportDto> reports = reportService.getReports(TEST_URID_1, ReportRequestingRole.administrator);

        assertThat(reports).hasSize(2);
        assertThat(reports)
            .extracting(ReportDto::getId)
            .containsOnly(report1.getId(), report2.getId());
        assertThat(reports)
            .extracting(ReportDto::getRequestingUser)
            .containsOnly(report1.getRequestingUser(), report2.getRequestingUser());
        assertThat(reports)
            .extracting(ReportDto::getStatus)
            .containsOnly(report1.getStatus(), report2.getStatus());
        assertThat(reports).extracting(d -> formatter.format(d.getRequestDate()))
            .containsOnly(formatter.format(now1), formatter.format(now2));
    }

    @Test
    public void shouldListAllReportsForAr() {

        LocalDateTime now1 = LocalDateTime.now();
        LocalDateTime now2 = LocalDateTime.now().plusSeconds(5L);
        Report report1 = generateTestReport(now1, null, TEST_URID_4, ReportStatus.PENDING, false);
        Report report2 = generateTestReport(now2, null, TEST_URID_4, ReportStatus.DONE, false);
        // this was created by another AR so it should not be retrieved
        generateTestReport(now2, null, TEST_URID_5, ReportStatus.DONE, false);

        List<ReportDto> reports = reportService.getReports(TEST_URID_4, null);

        assertThat(reports).hasSize(2);
        assertThat(reports)
            .extracting(ReportDto::getId)
            .containsOnly(report1.getId(), report2.getId());
        assertThat(reports)
            .extracting(ReportDto::getRequestingUser)
            .containsOnly(report1.getRequestingUser(), report2.getRequestingUser());
        assertThat(reports)
            .extracting(ReportDto::getStatus)
            .containsOnly(report1.getStatus(), report2.getStatus());
        assertThat(reports).extracting(d -> formatter.format(d.getRequestDate()))
            .containsOnly(formatter.format(now1), formatter.format(now2));
    }
    
    @Test
    @Ignore("until s3 integration is complete")
    public void shouldRetrieveFileContentOnDownload() {
        Report report = generateTestReport(LocalDateTime.now(), null, TEST_URID_1, ReportStatus.PENDING, true);

        ReportDto reportDto = reportService.downloadReport(report.getId());
        assertThat(reportDto.getData()).isNotEmpty();
    }

    @Test
    public void shouldThrowWhenDownloadingNonExistentReport() {

        assertThrows(UkEtsReportsException.class, () -> reportService.downloadReport(1L));
    }

    @Test
    public void shouldRetrieveExpiredReports() {
        LocalDateTime now = LocalDateTime.now();
        generateTestReport(now, now, TEST_URID_1, ReportStatus.DONE, true);
        Report report2 = generateTestReport(now, now.minusMinutes(1), TEST_URID_2, ReportStatus.DONE, true);
        Report report3 = generateTestReport(now, now.minusMinutes(1), TEST_URID_3, ReportStatus.FAILED, true);

        List<Report> reports = reportService.retrieveExpiredReports();

        assertThat(reports).hasSize(2);

        assertThat(reports).extracting(Report::getId).containsOnly(report2.getId(), report3.getId());

    }

    private Report generateTestReport(LocalDateTime requestDate, LocalDateTime generationDate, String urid,
                                      ReportStatus status, boolean isAdmin) {
        Report report = isAdmin ? constructTestReportForAdmin(requestDate, generationDate, urid, status)
                : constructTestReportForAr(requestDate, generationDate, urid, status);
        report = reportRepository.save(report);
        return report;
    }

    private Report constructTestReportForAdmin(
            LocalDateTime requestDate, LocalDateTime generationDate, String urid, ReportStatus status) {
        Report report = new Report();
        report.setType(ReportType.R0001);
        report.setRequestingRole(ReportRequestingRole.administrator);
        report.setRequestingUser(urid);
        report.setRequestDate(requestDate);
        report.setGenerationDate(generationDate);
        report.setFileLocation("/test/location/file.xlsx");
        report.setStatus(status);
        return report;
    }
    
    private Report constructTestReportForAr(
            LocalDateTime requestDate, LocalDateTime generationDate, String urid, ReportStatus status) {
        Report report = new Report();
        report.setType(ReportType.R0002);
        report.setRequestingRole(null);
        report.setRequestingUser(urid);
        report.setRequestDate(requestDate);
        report.setGenerationDate(generationDate);
        report.setFileLocation("/test/location/file.xlsx");
        report.setStatus(status);
        return report;
    }
}
