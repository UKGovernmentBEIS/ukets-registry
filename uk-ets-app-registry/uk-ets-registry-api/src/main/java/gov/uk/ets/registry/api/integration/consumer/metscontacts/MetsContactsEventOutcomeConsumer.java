package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public abstract class MetsContactsEventOutcomeConsumer {

    private final ErrorNotificationProducer errorNotificationProducer;
    private final MetsContactsNotificationService notificationService;

    @KafkaHandler
    @Transactional
    public void processOutcome(MetsContactsEventOutcome outcome, @Headers Map<String, Object> headers)
            throws NoSuchAlgorithmException {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            String operatorId = Optional.of(outcome.getEvent())
                    .map(MetsContactsEvent::getOperatorId)
                    .orElse(null);
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Operator ID",
                    operatorId, OperationEvent.UPDATE_ACCOUNT_METS_CONTACT_DETAILS, headers);
            return;
        }
        //Trigger claim account and send notifications
        notificationService.sendInvitation(outcome);
    }
}
