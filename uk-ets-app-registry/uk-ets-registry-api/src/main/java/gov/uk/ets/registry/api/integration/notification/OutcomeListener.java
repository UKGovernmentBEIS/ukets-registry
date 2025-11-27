package gov.uk.ets.registry.api.integration.notification;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import gov.uk.ets.registry.api.integration.message.AccountEmissionsUpdateEventOutcome;
import gov.uk.ets.registry.api.integration.message.IntegrationEventOutcome;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
@RequiredArgsConstructor
@KafkaListener(
    containerFactory = "outcomeConsumerFactory",
    topics = { "installation-account-emissions-updated-response-topic", "aviation-account-emissions-updated-response-topic", "maritime-emissions-updated-response-topic" }
)
@ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled"}, havingValue = "true")
public class OutcomeListener {

    private final ErrorNotificationProducer errorNotificationProducer;

    @KafkaHandler
    @Transactional
    public void processOutcome(AccountEmissionsUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            List<IntegrationEventErrorDetails> errorDetails = outcome.getErrors().stream()
                .map(error -> new IntegrationEventErrorDetails(error, null))
                .toList();
            errorNotificationProducer.sendNotifications(outcome.getEvent(), errorDetails, "Operator ID",
                outcome.getEvent().getRegistryId(), OperationEvent.UPDATE_EMISSIONS_VALUE, headers);
        }
    }
}
