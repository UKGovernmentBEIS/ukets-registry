package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        containerFactory = "regulatorNoticeOutcomeConsumerFactory",
        topics = {"${kafka.integration.maritime.regulator.notice.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.maritime.regulator.notice.enabled"}, havingValue = "true")
public class MaritimeRegulatorNoticeEventOutcomeConsumer extends RegulatorNoticeEventOutcomeConsumer {

    public MaritimeRegulatorNoticeEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer) {
        super(errorNotificationProducer);
    }
}
