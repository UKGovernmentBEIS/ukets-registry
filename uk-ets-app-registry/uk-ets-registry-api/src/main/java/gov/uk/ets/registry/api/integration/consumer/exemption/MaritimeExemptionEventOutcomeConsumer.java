package gov.uk.ets.registry.api.integration.consumer.exemption;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;

import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
@KafkaListener(
        containerFactory = "exemptionOutcomeConsumerFactory",
        topics = {"${kafka.integration.maritime.exemption.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.maritime.exemption.enabled"}, havingValue = "true")
public class MaritimeExemptionEventOutcomeConsumer {

    private final AccountExemptionEventOutcomeProcessor processor;

    @KafkaHandler
    @Transactional
    public void consumeMaritime(AccountExemptionUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {
        processor.processOutcome(outcome, headers);
    }
}
