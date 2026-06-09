package gov.uk.ets.publication.api.messaging;

import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class PublicationApiOutcomeListenerErrorHandler implements KafkaListenerErrorHandler {

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {

        if (message.getPayload() instanceof ReportGenerationEvent) {
        	ReportGenerationEvent payload = (ReportGenerationEvent) message.getPayload();
            log.error("Error consuming ReportGenerationEvent payload:",payload.toString());
        }
        Throwable cause = exception.getCause();
        log.error(cause.getMessage(), cause);
        
        return null;
    }

}
