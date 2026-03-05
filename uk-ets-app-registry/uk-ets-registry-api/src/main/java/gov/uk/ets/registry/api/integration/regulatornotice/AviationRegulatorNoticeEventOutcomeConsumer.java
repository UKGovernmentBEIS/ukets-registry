package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        containerFactory = "regulatorNoticeOutcomeConsumerFactory",
        topics = {"${kafka.integration.aviation.regulator.notice.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.aviation.regulator.notice.enabled"}, havingValue = "true")
public class AviationRegulatorNoticeEventOutcomeConsumer extends RegulatorNoticeEventOutcomeConsumer {

    public AviationRegulatorNoticeEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer) {
        super(errorNotificationProducer);
    }
}
