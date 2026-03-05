package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "regulatorNoticeOutcomeConsumerFactory",
        topics = {"${kafka.integration.installation.regulator.notice.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.installation.regulator.notice.enabled"}, havingValue = "true")
public class InstallationRegulatorNoticeEventOutcomeConsumer extends RegulatorNoticeEventOutcomeConsumer {

    public InstallationRegulatorNoticeEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer) {
        super(errorNotificationProducer);
    }
}
