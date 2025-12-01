package gov.uk.ets.registry.api.integration.consumer.operator;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import lombok.extern.log4j.Log4j2;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "operatorSetIdOutcomeConsumerFactory",
        topics = {"${kafka.integration.maritime.set.operator.response.topic}"}
    )
@ConditionalOnProperty(name = {"kafka.integration.enabled","kafka.integration.maritime.set.operator.enabled"}, havingValue = "true")
public class MaritimeOperatorSetIdOutcomeConsumer extends OperatorSetIdOutcomeConsumer {

    public MaritimeOperatorSetIdOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer) {
        super(errorNotificationProducer);
    }

    
    /**
     * Handles maritime set operator id outcome.
     * 
     * @param outcome
     * @param headers
     */
	@KafkaHandler
    @Transactional
    public void consumeMaritime(OperatorUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {
        processOutcome(outcome, headers);
    }
}
