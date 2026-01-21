package gov.uk.ets.registry.api.integration.consumer.emission;

import gov.uk.ets.registry.api.integration.consumer.KafkaMessageUtil;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.emission.EmissionEventService;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;

@Service
@Log4j2
@KafkaListener(
    containerFactory = "aviationEmissionsConsumerFactory",
    topics = "aviation-account-emissions-updated-request-topic"
)
@ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.aviation.emissions.enabled"}, havingValue = "true")
public class AviationEmissionEventConsumer {

    private final EmissionEventService emissionEventService;
    private final KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> kafkaTemplate;
    private final KafkaMessageUtil util;

    public AviationEmissionEventConsumer(EmissionEventService emissionEventService,
                                         @Qualifier("aviationEmissionsOutcomeKafkaTemplate") KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> kafkaTemplate,
                                         KafkaMessageUtil util) {
        this.emissionEventService = emissionEventService;
        this.kafkaTemplate = kafkaTemplate;
        this.util = util;
    }

    @KafkaHandler
    @Transactional
    public void processEmissionEvent(AccountEmissionsUpdateEvent event, @Headers Map<String, Object> headers) {
        List<IntegrationEventError> errors = emissionEventService.process(event, headers, SourceSystem.METSIA_AVIATION);
        kafkaTemplate.send(util.buildKafkaMessage(event, headers, errors));
    }
}