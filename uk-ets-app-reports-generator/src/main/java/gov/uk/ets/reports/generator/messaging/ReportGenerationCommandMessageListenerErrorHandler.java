package gov.uk.ets.reports.generator.messaging;

import gov.uk.ets.reports.generator.ReportGeneratorException;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

@Log4j2
@RequiredArgsConstructor
public class ReportGenerationCommandMessageListenerErrorHandler implements KafkaListenerErrorHandler {

    private final ReportOutcomeMessageService reportOutcomeMessageService;

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        ReportGenerationEvent reportGenerationEvent = ReportGenerationEvent.builder()
            .status(ReportStatus.FAILED)
            .build();
        if (message.getPayload() instanceof ReportGenerationCommand) {
            ReportGenerationCommand payload = (ReportGenerationCommand) message.getPayload();
            log.error(payload.toString());
            reportGenerationEvent.setId(payload.getId());
            reportGenerationEvent.setRequestingSystem(payload.getRequestingSystem());
        }
        Throwable cause = exception.getCause();

        if (cause instanceof ReportGeneratorException) {
            log.error(cause.getMessage(), cause);
        } else {
            log.error("Unhandled exception", cause);
        }
        reportOutcomeMessageService.sendReportOutcome(reportGenerationEvent);
        return null;
    }

}
