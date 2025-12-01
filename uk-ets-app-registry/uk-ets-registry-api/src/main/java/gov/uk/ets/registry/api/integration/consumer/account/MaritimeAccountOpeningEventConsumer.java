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
    topics = {"${kafka.integration.maritime.account.opening.request.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.maritime.account.opening.enabled"}, havingValue = "true")
public class MaritimeAccountOpeningEventConsumer extends AccountOpeningEventConsumer {
    
    @Value("${kafka.integration.maritime.account.opening.response.topic}")
    private String maritimeAccountOpeningResponseTopic;
    
    public MaritimeAccountOpeningEventConsumer(AccountEventOpeningService service,
                                               @Qualifier("accountOpeningOutcomeKafkaTemplate")
                                               KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate) {
        super(service, kafkaTemplate);
    }
    
    /**
     * Handles maritime account creation.
     * @param event
     * @param headers
     */
    @KafkaHandler
    @Transactional
    public void consumeMaritime(AccountOpeningEvent event, @Headers Map<String, Object> headers) {
        processEvent(event, headers, maritimeAccountOpeningResponseTopic);
    }    
	
}
