package gov.uk.ets.registry.api.integration.consumer.account.update;

import gov.uk.ets.registry.api.integration.consumer.account.EventConsumerTopicsUtil;
import gov.uk.ets.registry.api.integration.service.account.AccountEventUpdatingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;

import java.util.Map;

@Service
@Log4j2
@ConditionalOnProperty(
        name = "kafka.integration.aviation.account.updating.enabled",
        havingValue = "true"
)
public class AviationAccountUpdatingEventConsumer extends AccountUpdatingEventConsumer {

    public AviationAccountUpdatingEventConsumer(AccountEventUpdatingService service,
                                                KafkaTemplate<String, AccountUpdatingEventOutcome> kafkaTemplate,
                                                EventConsumerTopicsUtil topicsUtil) {
        super(service, kafkaTemplate, topicsUtil);
    }

    @KafkaListener(
            containerFactory = "aviationUpdatingConsumerFactory",
            topics = "${kafka.integration.aviation.account.updating.request.topic}"
    )
    @Transactional
    public void aviationUpdatingConsumerFactory(AccountUpdatingEvent event,
                                @Headers Map<String, Object> headers) {
        processEvent(event, headers);
    }

}
