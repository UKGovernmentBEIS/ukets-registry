package gov.uk.ets.registry.api.integration.consumer.account;

import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.account.validation.Violation;
import gov.uk.ets.registry.api.integration.service.account.AccountEventOpeningService;
import gov.uk.ets.registry.api.integration.service.account.AccountModificationResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@Log4j2
public abstract class AccountOpeningEventConsumer {

    private final AccountEventOpeningService service;
    private final KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate;

    private final KafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();

    public AccountOpeningEventConsumer(AccountEventOpeningService service,
                                       KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate
                                       ) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void processEvent(AccountOpeningEvent event, @Headers Map<String, Object> headers,String resultTopic) {

        AccountModificationResult result;
        try {
            result = service.process(event, headers);
        } catch (AccountValidationException exception) {
            log.error("Failed to create account with validation error.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0100,
                    exception.getErrors().stream().map(Violation::getMessage).collect(Collectors.joining(", "))));
            result = new AccountModificationResult(internalErrors);
        } catch (Exception exception) {
            log.error("Failed to create account.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0100, "Failed to create account."));
            result = new AccountModificationResult(internalErrors);
        }

        kafkaTemplate.send(buildKafkaMessage(event, headers, result, resultTopic));
    }


    private ProducerRecord<String, AccountOpeningEventOutcome> buildKafkaMessage(AccountOpeningEvent event,
                                                                                 Map<String, Object> headers,
                                                                                 AccountModificationResult result,
                                                                                 String resultTopic

    ) {
        IntegrationEventOutcome outcome = result.getErrors().isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        String key = Optional.ofNullable(result.getAccountFullIdentifier()).map(Object::toString).orElse("");
        AccountOpeningEventOutcome eventOutcome =
            new AccountOpeningEventOutcome(event, result.getAccountFullIdentifier(), result.getErrors(), outcome);

        RecordHeaders target = new RecordHeaders();
        mapper.fromHeaders(new MessageHeaders(headers), target);

        return new ProducerRecord<>(resultTopic, null, key, eventOutcome, target);
    }
}
