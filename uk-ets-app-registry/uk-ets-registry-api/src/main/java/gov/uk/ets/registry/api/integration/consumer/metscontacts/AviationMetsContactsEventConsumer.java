package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.integration.consumer.account.EventConsumerTopicsUtil;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsEventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.util.Map;

@Service
@Log4j2
@ConditionalOnProperty(
        name = "kafka.integration.aviation.mets.contacts.enabled",
        havingValue = "true"
)
public class AviationMetsContactsEventConsumer extends MetsContactsEventConsumer {

    public AviationMetsContactsEventConsumer(MetsContactsEventService service, KafkaTemplate<String, MetsContactsEventOutcome> kafkaTemplate, EventConsumerTopicsUtil topicsUtil) {
        super(service, kafkaTemplate, topicsUtil);
    }

    @KafkaListener(
            containerFactory = "aviationMetsContactsConsumerFactory",
            topics = "${kafka.integration.aviation.mets.contacts.request.topic}"
    )
    @Transactional
    public void aviationMetsContactsConsumerFactory(MetsContactsEvent event,
                                                    @Headers Map<String, Object> headers) {
        processEvent(event, headers);
    }

}
