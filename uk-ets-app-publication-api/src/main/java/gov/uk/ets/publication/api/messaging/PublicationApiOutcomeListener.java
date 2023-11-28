package gov.uk.ets.publication.api.messaging;

import gov.uk.ets.publication.api.service.SectionService;
import gov.uk.ets.reports.model.RequestingSystem;
import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@KafkaListener(topics = "${kafka.report.outcome.topic:report.outcome.topic}",
    containerFactory = "publicationApiListenerContainerFactory")
@RequiredArgsConstructor
@Service
public class PublicationApiOutcomeListener {

    private final SectionService sectionService;

    @KafkaHandler
    public void processReportOutcome(ReportGenerationEvent event) {
        if (RequestingSystem.PUBLICATION_API.equals(event.getRequestingSystem())) {
        	sectionService.processReportEvent(event);
        }
    }
}
