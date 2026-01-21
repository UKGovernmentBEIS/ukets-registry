package gov.uk.ets.registry.api.integration.consumer.account.update;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
	    containerFactory = "accountUpdatingOutcomeConsumerFactory",
	    topics = {"${kafka.integration.aviation.account.updating.response.topic}"}
	)
@ConditionalOnProperty(name = {"kafka.integration.aviation.account.updating.enabled"}, havingValue = "true")
public class AviationAccountUpdatingEventOutcomeConsumer extends AccountUpdatingEventOutcomeConsumer {

	public AviationAccountUpdatingEventOutcomeConsumer(GroupNotificationClient groupNotificationClient,
                                                       ErrorNotificationProducer errorNotificationProducer, IntegrationHeadersUtil util,
                                                       AccountRepository accountRepository) {
		super(groupNotificationClient, errorNotificationProducer, util, accountRepository);
	}
}
