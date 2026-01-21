package gov.uk.ets.registry.api.integration.consumer.metscontacts;

import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.metscontacts.MetsContactsNotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "metsContactsOutcomeConsumerFactory",
        topics = {"${kafka.integration.installation.mets.contacts.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.installation.mets.contacts.enabled"}, havingValue = "true")
public class InstallationMetsContactsEventOutcomeConsumer extends MetsContactsEventOutcomeConsumer {

	public InstallationMetsContactsEventOutcomeConsumer(ErrorNotificationProducer errorNotificationProducer,
														MetsContactsNotificationService notificationService) {
		super(errorNotificationProducer, notificationService);
	}
}
