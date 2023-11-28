package gov.uk.ets.publication.api.service;

import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.error.UkEtsPublicationApiException;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReportGenerationService {

    @Value("${kafka.report.request.topic}")
    private String reportRequestTopic;
    
    private final ReportFilesRepository reportFilesRepository;

    private final KafkaTemplate<String, Serializable> publicationApiKafkaTemplate;

    @Transactional
    public void requestReport(ReportType reportType, Long id, LocalDateTime date, ReportQueryInfo reportQueryInfo) {
        ReportQueryInfoWithMetadata queryWithMetadata =
            ReportQueryInfoWithMetadata.builder()
                                       .query(reportQueryInfo.getQuery())
                                       .from(reportQueryInfo.getFrom())
                                       .to(reportQueryInfo.getTo())
                                       .year(reportQueryInfo.getYear())
                                       .commitmentPeriod(reportQueryInfo.getCommitmentPeriod())
                                       .build();

        ReportGenerationCommand message = ReportGenerationCommand.builder()
                                                                 .id(id)
                                                                 .type(reportType)
                                                                 .requestDate(date)
                                                                 .reportQueryInfo(queryWithMetadata)
                                                                 .requestingSystem(RequestingSystem.PUBLICATION_API)
                                                                 .build();
        try {
            publicationApiKafkaTemplate.send(reportRequestTopic, message).get();
        } catch (Exception e) {
            log.error("ReportGenerationCommand message failed to send: {}", message);
            throw new RuntimeException(e);
        }
    }
}
