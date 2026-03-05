package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;

import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public abstract class RegulatorNoticeEventOutcomeConsumer {

    private final ErrorNotificationProducer errorNotificationProducer;

    @KafkaHandler
    @Transactional
    public void processOutcome(RegulatorNoticeEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            String registryId = Optional.of(outcome.getEvent())
                    .map(RegulatorNoticeEvent::getRegistryId)
                    .orElse(null);
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Registry ID",
                    registryId, OperationEvent.REGULATOR_NOTICE, headers);
        }
    }
}
