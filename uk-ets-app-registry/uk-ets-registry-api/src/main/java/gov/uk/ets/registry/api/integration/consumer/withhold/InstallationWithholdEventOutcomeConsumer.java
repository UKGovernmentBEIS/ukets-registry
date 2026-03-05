package gov.uk.ets.registry.api.integration.consumer.withhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;

import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
@KafkaListener(
        containerFactory = "withholdOutcomeConsumerFactory",
        topics = {"${kafka.integration.installation.withhold.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.installation.withhold.enabled"}, havingValue = "true")
public class InstallationWithholdEventOutcomeConsumer {

    private final AccountWithholdEventOutcomeProcessor processor;

    @KafkaHandler
    @Transactional
    public void consumeInstallation(AccountWithholdUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {
        processor.processOutcome(outcome, headers);
    }
}
