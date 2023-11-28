package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.reports.generator.messaging.ReportOutcomeMessageService;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DownloadFile {

    @Value("${aws.s3.reports.bucket.name}")
    private String awsS3ReportsBucketName;

    private final S3ClientService s3ClientService;
    private final ReportOutcomeMessageService reportOutcomeMessageService;

    public void getFile(KyotoReportOutcome outcome, KyotoProtocolParams params) {

        s3ClientService.uploadFile(awsS3ReportsBucketName, outcome.getFileName(), outcome.getContent() , outcome.getContentType().getValue());

        reportOutcomeMessageService.sendReportOutcome(
                ReportGenerationEvent.builder()
                        .id(params.getReportGeneratorCommandId())
                        .status(ReportStatus.DONE)
                        .requestingSystem(params.getRequestingSystem())
                        .fileSize(outcome.getContent().length / 1024L)
                        .generationDate(LocalDateTime.now())
                        .fileLocation(String.format("s3:%s/%s", awsS3ReportsBucketName, outcome.getFileName()))
                        .build());
    }
}
