package gov.uk.ets.reports.api;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.reports.api.domain.Report;
import gov.uk.ets.reports.api.error.UkEtsReportsException;
import gov.uk.ets.reports.api.web.model.ReportCreationRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationResponse;
import gov.uk.ets.reports.api.web.model.ReportDto;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReportService {
    private static final Sort.TypedSort<Report> sortingReport = Sort.sort(Report.class);

    @Value("${aws.s3.reports.bucket.name}")
    private String bucketName;
    @Value("${kafka.report.request.topic}")
    private String reportRequestTopic;
    @Value("${reports.expiration.minutes}")
    private Long reportsExpirationInMinutes;

    @Value("${pending.reports.expiration.minutes}")
    private Long pendingReportsExpirationInMinutes;

    private final ReportRepository reportRepository;
    private final S3ClientService s3ClientService;
    private final KafkaTemplate<String, Serializable> reportsApiKafkaTemplate;

    /**
     * Saves report request in database and dispatch a report creation command in kafka.
     */
    @Transactional
    public ReportCreationResponse requestReport(ReportCreationRequest request, String currentUrid) {

        Report report = new Report();
        report.setType(request.getType());
        report.setRequestingUser(currentUrid);
        report.setRequestDate(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);
        report.setRequestingRole(request.getRequestingRole());
        // only standard reports contain criteria for serialization
        if (request.getReportQueryInfo().getQuery() == null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                report.setCriteria(mapper.writeValueAsString(request.getReportQueryInfo()));
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Error during JSON serialisation", exception);
            }
        }
        Report savedReport = reportRepository.save(report);

        ReportQueryInfo queryInfo = request.getReportQueryInfo();
        ReportQueryInfoWithMetadata queryWithMetadata = ReportQueryInfoWithMetadata.builder()
                .query(queryInfo.getQuery())
                .from(queryInfo.getFrom())
                .to(queryInfo.getTo())
                .year(queryInfo.getYear())
                .commitmentPeriod(queryInfo.getCommitmentPeriod())
                .urid(currentUrid)
                .requestingRole(savedReport.getRequestingRole())
                .cutOffTime(queryInfo.getCutOffTime())
                .regulators(queryInfo.getRegulators())
                .transactionIdentifier(queryInfo.getTransactionIdentifier())
                .accountFullIdentifier(queryInfo.getAccountFullIdentifier())
                .build();

        ReportGenerationCommand message = ReportGenerationCommand.builder()
            .id(report.getId())
            .type(report.getType())
            .requestDate(report.getRequestDate())
            .reportQueryInfo(queryWithMetadata)
            .requestingSystem(RequestingSystem.REPORTS_API)
            .build();
        try {
            reportsApiKafkaTemplate.send(reportRequestTopic, message).get();
        } catch (Exception e) {
            log.error("ReportGenerationCommand message failed to be send: {}", message);
            throw new RuntimeException(e);
        }

        return ReportCreationResponse.builder().reportId(savedReport.getId()).build();

    }

    /**
     * Retrieve all reports metadata sorted by the request date.
     * @param urid
     * @param role
     */
    public List<ReportDto> getReports(String urid, ReportRequestingRole role) {
        if (role != null) {
            return reportRepository
                    .findByRequestingRole(role, sortingReport.by(Report::getRequestDate).descending()).stream()
                    .map(this::toDto)
                    .collect(toList());
        } else {
            return reportRepository
                    .findByRequestingUser(urid, sortingReport.by(Report::getRequestDate).descending()).stream()
                    .map(this::toDto)
                    .collect(toList());
        }
    }

    /**
     * Retrieve specific report metadata  with its content (for specific id).
     */
    public ReportDto downloadReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new UkEtsReportsException(String.format("report with id %s not found", reportId)));

        try (ResponseInputStream<GetObjectResponse> responseInputStream = s3ClientService
            .downloadFile(bucketName, report.getFileName())) {
            return toDtoWithContent(report, responseInputStream.readAllBytes());
        } catch (Exception e) {
            throw new UkEtsReportsException(
                String.format("Retrieving report %s from bucket %s failed. Cause: %s", report.getFileName(), bucketName,
                    e.getCause().getMessage()));
        }
    }

    /**
     * Updates report in database with report generation metadata.
     */
    @Transactional
    public void processReportEvent(ReportGenerationEvent event) {
        if (event.getStatus() == ReportStatus.DONE) {
            processGeneratedReport(event);
        } else if (event.getStatus() == ReportStatus.FAILED) {
            processFailedReport(event);
        }
    }

    /**
     * Retrieves expired reports. Pending reports are not considered expired.
     */
    public List<Report> retrieveExpiredReports() {
        LocalDateTime dateTimeExpiration = LocalDateTime.now().minusMinutes(reportsExpirationInMinutes);
        return reportRepository.findByGenerationDateBeforeAndStatusNot(dateTimeExpiration, ReportStatus.PENDING);
    }

    /**
     * Retrieves PENDING reports to expire. PENDING reports do not have a generation date so we need
     * to check the request date.
     */
    public List<Report> retrievePendingReportsToExpire() {
        LocalDateTime dateTimeExpiration = LocalDateTime.now().minusMinutes(pendingReportsExpirationInMinutes);
        return reportRepository.findByRequestDateBeforeAndStatus(dateTimeExpiration, ReportStatus.PENDING);
    }

    public void deleteReport(Report report) {
        reportRepository.delete(report);
    }

    private ReportDto toDto(Report report) {
        return reportDtoBuilder(report)
            .build();
    }

    private ReportDto toDtoWithContent(Report report, byte[] data) {
        return reportDtoBuilder(report).data(data).build();
    }

    private ReportDto.ReportDtoBuilder reportDtoBuilder(Report report) {
        return ReportDto.builder()
            .id(report.getId())
            .type(report.getType())
            .status(report.getStatus())
            .requestingUser(report.getRequestingUser())
            .requestDate(report.getRequestDate())
            .generationDate(report.getGenerationDate())
            .expirationDate(calculateExpirationDate(report))
            .fileName(report.getFileName())
            .fileSize(report.getFileSize());
    }

    private LocalDateTime calculateExpirationDate(Report report) {
        LocalDateTime generationDate = report.getGenerationDate();
        return generationDate != null ? generationDate.plusMinutes(reportsExpirationInMinutes) : null;
    }

    private void processGeneratedReport(ReportGenerationEvent event) {
        Report report = reportRepository.findById(event.getId())
            .orElseThrow(() -> new UkEtsReportsException(String.format("Report with id %s not found.", event.getId())));

        report.setStatus(ReportStatus.DONE);
        report.setFileLocation(event.getFileLocation());
        report.setFileSize(event.getFileSize());
        report.setGenerationDate(event.getGenerationDate());
    }


    private void processFailedReport(ReportGenerationEvent event) {
        Report report = reportRepository.findById(event.getId())
            .orElseThrow(() -> new UkEtsReportsException(String.format("Report with id %s not found.", event.getId())));

        report.setStatus(ReportStatus.FAILED);
        report.setGenerationDate(LocalDateTime.now());
    }
}
