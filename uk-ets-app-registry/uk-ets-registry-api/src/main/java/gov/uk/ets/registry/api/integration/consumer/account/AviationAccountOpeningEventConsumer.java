package gov.uk.ets.registry.api.integration.consumer.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.integration.service.account.AccountEventOpeningService;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;

@Component
@KafkaListener(
    containerFactory = "accountOpeningConsumerFactory",
    topics = {"${kafka.integration.aviation.account.opening.request.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.aviation.account.opening.enabled"}, havingValue = "true")
public class AviationAccountOpeningEventConsumer extends AccountOpeningEventConsumer {
    
    @Value("${kafka.integration.aviation.account.opening.response.topic}")
    private String aviationAccountOpeningResponseTopic;
    
    public AviationAccountOpeningEventConsumer(AccountEventOpeningService service,
                                               @Qualifier("accountOpeningOutcomeKafkaTemplate")
                                               KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate) {
        super(service, kafkaTemplate);
    }
    
    /**
     * Handles aviation account creation.
     * @param event
     * @param headers
     */
    @KafkaHandler
    @Transactional
    public void consumeAviation(AccountOpeningEvent event, @Headers Map<String, Object> headers) {
        processEvent(event, headers, aviationAccountOpeningResponseTopic);
    }    
}
