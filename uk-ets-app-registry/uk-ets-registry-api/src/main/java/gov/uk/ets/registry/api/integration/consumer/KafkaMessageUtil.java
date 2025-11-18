package gov.uk.ets.registry.api.integration.consumer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;

@Component
public class KafkaMessageUtil {

    public Message<AccountEmissionsUpdateEventOutcome> buildKafkaMessage(AccountEmissionsUpdateEvent event,
                                                                         Map<String, Object> headers,
                                                                         List<IntegrationEventError> errors) {
        IntegrationEventOutcome result = errors.isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        AccountEmissionsUpdateEventOutcome outcome = new AccountEmissionsUpdateEventOutcome(event, errors, result);
        String registryId = Optional.ofNullable(event.getRegistryId()).map(Object::toString).orElse("");

        return MessageBuilder.withPayload(outcome)
            .copyHeaders(headers)
            .setHeader(KafkaHeaders.KEY, registryId)
            .build();
    }
}
