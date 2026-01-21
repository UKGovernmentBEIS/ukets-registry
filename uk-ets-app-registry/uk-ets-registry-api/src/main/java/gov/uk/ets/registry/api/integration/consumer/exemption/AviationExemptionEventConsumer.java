package gov.uk.ets.registry.api.integration.consumer.exemption;

import gov.uk.ets.registry.api.integration.consumer.KafkaMessageUtil;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.exemption.ExemptionEventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@KafkaListener(
        containerFactory = "exemptionConsumerFactory",
        topics = "${kafka.integration.aviation.exemption.request.topic}"
)
@ConditionalOnProperty(name = {"kafka.integration.aviation.exemption.enabled"}, havingValue = "true")
public class AviationExemptionEventConsumer {

    private final ExemptionEventService exemptionEventService;
    private final KafkaTemplate<String, AccountExemptionUpdateEventOutcome> kafkaTemplate;
    private final KafkaMessageUtil util;

    @Value("${kafka.integration.aviation.exemption.response.topic}")
    private String aviationExemptionResponseTopic;

    public AviationExemptionEventConsumer(ExemptionEventService exemptionEventService,
                                         @Qualifier("exemptionOutcomeKafkaTemplate") KafkaTemplate<String,
                                                 AccountExemptionUpdateEventOutcome> kafkaTemplate,
                                         KafkaMessageUtil util) {
        this.exemptionEventService = exemptionEventService;
        this.kafkaTemplate = kafkaTemplate;
        this.util = util;
    }

    @KafkaHandler
    @Transactional
    public void processExemptionEvent(AccountExemptionUpdateEvent event, @Headers Map<String, Object> headers) {
        List<IntegrationEventErrorDetails> errors = exemptionEventService.process(event, headers, SourceSystem.METSIA_AVIATION);
        kafkaTemplate.send(util.buildKafkaMessage(event, headers, errors, aviationExemptionResponseTopic));
    }
}
