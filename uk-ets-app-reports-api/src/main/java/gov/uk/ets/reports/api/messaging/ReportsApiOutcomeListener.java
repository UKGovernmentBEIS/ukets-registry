package gov.uk.ets.reports.api.messaging;

import gov.uk.ets.reports.api.ReportService;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@KafkaListener(topics = "${kafka.report.outcome.topic:report.outcome.topic}",
    containerFactory = "reportsApiListenerContainerFactory")
@RequiredArgsConstructor
@Service
public class ReportsApiOutcomeListener {

    private final ReportService reportService;

    @KafkaHandler
    public void processReportOutcome(ReportGenerationEvent event) {
        if (RequestingSystem.REPORTS_API.equals(event.getRequestingSystem())) {
            reportService.processReportEvent(event);
        }
    }
}
