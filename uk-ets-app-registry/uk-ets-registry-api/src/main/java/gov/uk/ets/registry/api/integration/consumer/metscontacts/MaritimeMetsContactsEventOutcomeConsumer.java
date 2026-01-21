package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsNotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
	    containerFactory = "metsContactsOutcomeConsumerFactory",
	    topics = {"${kafka.integration.maritime.mets.contacts.response.topic}"}
	)
@ConditionalOnProperty(name = {"kafka.integration.maritime.mets.contacts.enabled"}, havingValue = "true")
public class MaritimeMetsContactsEventOutcomeConsumer extends MetsContactsEventOutcomeConsumer {

	public MaritimeMetsContactsEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer,
													MetsContactsNotificationService notificationService) {
		super(errorNotificationProducer, notificationService);
	}
}
