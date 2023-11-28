package gov.uk.ets.reports.generator.messaging;

import gov.uk.ets.reports.generator.export.ReportGeneratorService;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolReportGeneratorService;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.ConfigUtil;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;

@Log4j2
@RequiredArgsConstructor
public class ReportGenerationCommandMessageListener {

    private final ReportGeneratorService reportGeneratorService;
    private final KyotoProtocolReportGeneratorService kyotoProtocolReportGeneratorService;

    /**
     * Handles the incoming message request.
     *
     * @param request The message request.
     */
    @KafkaListener(
        containerFactory = "reportGenerationCommandConsumerFactory",
        topics = "report.request.topic",
        errorHandler = "reportGenerationCommandMessageListenerErrorHandler"
    )
    public void handleReportGenerationCommand(ReportGenerationCommand request) {
        log.info("Received a reporting command :{} ", request.toString());
        if (ConfigUtil.isKyotoProtocolReport(request.getType())) {
            kyotoProtocolReportGeneratorService.generateKyotoProtocolReport(request);
        } else {
            reportGeneratorService.generateReport(request);
        }
    }
}
