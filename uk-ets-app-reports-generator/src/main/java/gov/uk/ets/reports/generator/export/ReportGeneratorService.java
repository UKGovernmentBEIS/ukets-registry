package gov.uk.ets.reports.generator.export;

import gov.uk.ets.commons.s3.client.S3ClientService;
import gov.uk.ets.reports.generator.messaging.ReportOutcomeMessageService;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.ReportType.ReportFormat;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//TODO maybe change name to ReportGeneratorOrchestrator and move to other package
public class ReportGeneratorService {

    private final ReportOutcomeMessageService reportOutcomeMessageService;

    @Value("${aws.s3.reports.bucket.name}")
    private String awsS3ReportsBucketName;

    private final ExcelReportService excelReportService;
    
    private final PdfReportR0034Service pdfReportService;

    private final PdfReportR0035Service pdfReportR0035Service;
    
    private final S3ClientService s3ClientService;

    public void generateReport(ReportGenerationCommand command) {
        String filename =
            ReportFileGeneratorHelper.generateFileName(
                    command.getType(), command.getReportQueryInfo().getYear(), command.getRequestDate());

        byte[] reportBytes;
        
        //TODO stream for large report instead of writing in the server
        if (ReportFormat.EXCEL.equals(command.getType().getFormat())) {
            reportBytes = excelReportService.writeReport(command);
            s3ClientService.uploadXlsxFile(awsS3ReportsBucketName, filename, reportBytes);            
        } else if (ReportType.R0034.equals(command.getType())) {
            reportBytes = pdfReportService.writeReport(command);
            s3ClientService.uploadPdfFile(awsS3ReportsBucketName, filename, reportBytes);  
        } else if (ReportType.R0035.equals(command.getType())) {
            reportBytes = pdfReportR0035Service.writeReport(command);
            s3ClientService.uploadPdfFile(awsS3ReportsBucketName, filename, reportBytes);              
        } else {
            throw new IllegalArgumentException("Invalid Report Type or Format");
        }


        reportOutcomeMessageService.sendReportOutcome(
            ReportGenerationEvent.builder()
                .id(command.getId())
                .status(ReportStatus.DONE)
                .requestingSystem(command.getRequestingSystem())
                .fileSize(reportBytes.length / 1024L)
                .generationDate(LocalDateTime.now())
                .fileLocation(String.format("s3:%s/%s", awsS3ReportsBucketName, filename))
                .build()
        );
    }


}
