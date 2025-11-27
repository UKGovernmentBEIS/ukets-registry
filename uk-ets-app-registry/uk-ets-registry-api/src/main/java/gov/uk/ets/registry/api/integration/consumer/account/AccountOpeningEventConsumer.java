package gov.uk.ets.registry.api.integration.consumer.account;

import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.account.validation.Violation;
import gov.uk.ets.registry.api.integration.error.IntegrationEventError;
import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import gov.uk.ets.registry.api.integration.message.AccountOpeningEvent;
import gov.uk.ets.registry.api.integration.message.AccountOpeningEventOutcome;
import gov.uk.ets.registry.api.integration.message.IntegrationEventOutcome;
import gov.uk.ets.registry.api.integration.service.account.AccountEventOpeningService;
import gov.uk.ets.registry.api.integration.service.account.AccountOpeningResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@KafkaListener(
    containerFactory = "accountOpeningConsumerFactory",
    topics = {"${kafka.integration.maritime.account.opening.request.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.account.opening.enabled"}, havingValue = "true")
public class AccountOpeningEventConsumer {

    private final AccountEventOpeningService service;
    private final KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate;

    private final KafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();

    // Other topics than maritime are not in used currently.
    @Value("${kafka.integration.installation.account.opening.request.topic}")
    private String installationAccountOpeningRequestTopic;
    @Value("${kafka.integration.aviation.account.opening.request.topic}")
    private String aviationAccountOpeningRequestTopic;
    @Value("${kafka.integration.maritime.account.opening.request.topic}")
    private String maritimeAccountOpeningRequestTopic;
    @Value("${kafka.integration.installation.account.opening.response.topic}")
    private String installationAccountOpeningResponseTopic;
    @Value("${kafka.integration.aviation.account.opening.response.topic}")
    private String aviationAccountOpeningResponseTopic;
    @Value("${kafka.integration.maritime.account.opening.response.topic}")
    private String maritimeAccountOpeningResponseTopic;

    public AccountOpeningEventConsumer(AccountEventOpeningService service,
                                       @Qualifier("accountOpeningOutcomeKafkaTemplate")
                                       KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate) {
        this.service = service ;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    @Transactional
    public void processAccountOpeningEvent(AccountOpeningEvent event, @Headers Map<String, Object> headers) {

        AccountOpeningResult result;
        try {
            result = service.process(event, headers);
        } catch (AccountValidationException exception) {
            log.error("Failed to create account with validation error.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0100,
                    exception.getErrors().stream().map(Violation::getMessage).collect(Collectors.joining(", "))));
            result = new AccountOpeningResult(internalErrors);
        } catch (Exception exception) {
            log.error("Failed to create account.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0100, "Failed to create account."));
            result = new AccountOpeningResult(internalErrors);
        }

        kafkaTemplate.send(buildKafkaMessage(event, headers, result));
    }

    private ProducerRecord<String, AccountOpeningEventOutcome> buildKafkaMessage(AccountOpeningEvent event,
                                                                                 Map<String, Object> headers,
                                                                                 AccountOpeningResult result) {
        IntegrationEventOutcome outcome = result.getErrors().isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        String key = Optional.ofNullable(result.getAccountFullIdentifier()).map(Object::toString).orElse("");
        AccountOpeningEventOutcome eventOutcome =
            new AccountOpeningEventOutcome(event, result.getAccountFullIdentifier(), result.getErrors(), outcome);

        String sourceTopic = headers.get(KafkaHeaders.RECEIVED_TOPIC).toString();

        RecordHeaders target = new RecordHeaders();
        mapper.fromHeaders(new MessageHeaders(headers), target);

        return new ProducerRecord<>(getResponseTopic(sourceTopic), null, key, eventOutcome, target);
    }

    private String getResponseTopic(String topic) {
        if (Objects.equals(topic, installationAccountOpeningRequestTopic)) {
            return installationAccountOpeningResponseTopic;
        }

        if (Objects.equals(topic, aviationAccountOpeningRequestTopic)) {
            return aviationAccountOpeningResponseTopic;
        }

        if (Objects.equals(topic, maritimeAccountOpeningRequestTopic)) {
            return maritimeAccountOpeningResponseTopic;
        }

        throw new IllegalStateException("Unknown Request topic");
    }
}
