package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.consumer.account.EventConsumerTopicsUtil;
import gov.uk.ets.registry.api.integration.service.regulatornotice.RegulatorNoticeEventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;

import java.util.Map;

@Service
@Log4j2
@ConditionalOnProperty(
        name = "kafka.integration.aviation.regulator.notice.enabled",
        havingValue = "true"
)
public class AviationRegulatorNoticeEventConsumer extends RegulatorNoticeEventConsumer {

    public AviationRegulatorNoticeEventConsumer(RegulatorNoticeEventService service,
                                                KafkaTemplate<String, RegulatorNoticeEventOutcome> kafkaTemplate,
                                                EventConsumerTopicsUtil topicsUtil) {
        super(service, kafkaTemplate, topicsUtil);
    }

    @KafkaListener(
            containerFactory = "aviationRegulatorNoticeConsumerFactory",
            topics = "${kafka.integration.aviation.regulator.notice.request.topic}"
    )
    @Transactional
    public void aviationRegulatorNoticeConsumerFactory(RegulatorNoticeEvent event,
                                                @Headers Map<String, Object> headers) {
        processEvent(event, headers);
    }
}
