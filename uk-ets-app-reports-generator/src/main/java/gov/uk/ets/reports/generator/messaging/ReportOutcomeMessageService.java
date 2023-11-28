package gov.uk.ets.reports.generator.messaging;

import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ReportOutcomeMessageService {


    @Value("${kafka.report.outcome.topic}")
    private String reportOutcomeTopic;

    private final KafkaTemplate<String, Serializable> reportProducerTemplate;

    /**
     * Constructor.
     */
    public ReportOutcomeMessageService(KafkaTemplate<String, Serializable> reportProducerTemplate) {
        this.reportProducerTemplate = reportProducerTemplate;
    }

    public void sendReportOutcome(ReportGenerationEvent event) {
        log.info("Sending a report outcome {}", event);
        try {
            reportProducerTemplate.send(reportOutcomeTopic, event).get();
        } catch (InterruptedException e) {
            log.error("ReportGenerationEvent message failed to be send interrupted: {}", event);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("ReportGenerationEvent message failed to be send: {}", event);
            throw new RuntimeException(e);
         }
    }
}
