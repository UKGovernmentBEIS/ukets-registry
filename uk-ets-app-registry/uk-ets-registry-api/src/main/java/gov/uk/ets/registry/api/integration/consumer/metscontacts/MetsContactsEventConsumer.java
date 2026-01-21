package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.account.validation.Violation;
import gov.uk.ets.registry.api.integration.consumer.account.EventConsumerTopicsUtil;
import gov.uk.ets.registry.api.integration.service.account.AccountModificationResult;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsEventService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public abstract class MetsContactsEventConsumer {

    private final MetsContactsEventService service;
    private final KafkaTemplate<String, MetsContactsEventOutcome> kafkaTemplate;
    private final EventConsumerTopicsUtil topicsUtil;
    private final KafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();

    public MetsContactsEventConsumer(MetsContactsEventService service,
                                        KafkaTemplate<String, MetsContactsEventOutcome> kafkaTemplate,
                                        EventConsumerTopicsUtil topicsUtil) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
        this.topicsUtil = topicsUtil;
    }

    public void processEvent(MetsContactsEvent event, @Headers Map<String, Object> headers) {
        AccountModificationResult result;
        try {
            result = service.process(event, headers);
        } catch (AccountValidationException exception) {
            log.error("Failed to update account by adding mets contacts with validation error.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                    List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0700,
                            exception.getErrors().stream().map(Violation::getMessage).collect(Collectors.joining(", "))));
            result = new AccountModificationResult(event.getOperatorId(), internalErrors);
        } catch (Exception exception) {
            log.error("Failed to update account by adding mets contacts.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                    List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0700, "Failed to update account by adding mets contacts."));
            result = new AccountModificationResult(event.getOperatorId(), internalErrors);
        }

        kafkaTemplate.send(buildKafkaMessage(event, headers, result));
    }

    private ProducerRecord<String, MetsContactsEventOutcome> buildKafkaMessage(MetsContactsEvent event,
                                                                               Map<String, Object> headers,
                                                                               AccountModificationResult result) {
        IntegrationEventOutcome outcome = result.getErrors().isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        String key = Optional.ofNullable(result.getAccountFullIdentifier()).map(Object::toString)
                .orElse("INVALID_OPERATOR");
        MetsContactsEventOutcome eventOutcome =
                new MetsContactsEventOutcome(event, result.getAccountFullIdentifier(), result.getErrors(), outcome);

        String sourceTopic = headers.get(KafkaHeaders.RECEIVED_TOPIC).toString();

        RecordHeaders target = new RecordHeaders();
        mapper.fromHeaders(new MessageHeaders(headers), target);

        return new ProducerRecord<>(topicsUtil.getResponseTopic(sourceTopic), null, key, eventOutcome, target);
    }
}
