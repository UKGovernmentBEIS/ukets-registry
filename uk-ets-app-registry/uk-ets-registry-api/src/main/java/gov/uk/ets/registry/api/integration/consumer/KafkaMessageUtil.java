package gov.uk.ets.registry.api.integration.consumer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;

@Component
public class KafkaMessageUtil {

    private final KafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();

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

    public ProducerRecord<String, AccountExemptionUpdateEventOutcome> buildKafkaMessage(AccountExemptionUpdateEvent event,
                                                                                        Map<String, Object> headers,
                                                                                        List<IntegrationEventErrorDetails> errors,
                                                                                        String responseTopic) {
        IntegrationEventOutcome result = errors.isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        AccountExemptionUpdateEventOutcome outcome = new AccountExemptionUpdateEventOutcome(event, errors, result);
        String registryId = Optional.ofNullable(event.getRegistryId()).map(Object::toString).orElse("");

        RecordHeaders targetHeaders = new RecordHeaders();
        mapper.fromHeaders(new MessageHeaders(headers), targetHeaders);

        return new ProducerRecord<>(responseTopic, null, registryId, outcome, targetHeaders);
    }
}
