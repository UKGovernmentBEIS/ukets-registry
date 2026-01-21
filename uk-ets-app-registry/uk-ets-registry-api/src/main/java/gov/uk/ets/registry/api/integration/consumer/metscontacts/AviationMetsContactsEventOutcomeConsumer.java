package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsNotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
	    containerFactory = "metsContactsOutcomeConsumerFactory",
	    topics = {"${kafka.integration.aviation.mets.contacts.response.topic}"}
	)
@ConditionalOnProperty(name = {"kafka.integration.aviation.mets.contacts.enabled"}, havingValue = "true")
public class AviationMetsContactsEventOutcomeConsumer extends MetsContactsEventOutcomeConsumer {

	public AviationMetsContactsEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer,
													MetsContactsNotificationService notificationService) {
		super(errorNotificationProducer, notificationService);
	}
}
