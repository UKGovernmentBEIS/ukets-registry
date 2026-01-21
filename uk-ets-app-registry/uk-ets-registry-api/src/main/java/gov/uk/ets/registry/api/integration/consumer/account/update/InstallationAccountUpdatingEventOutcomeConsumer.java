package gov.uk.ets.registry.api.integration.consumer.account.update;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "accountUpdatingOutcomeConsumerFactory",
        topics = {"${kafka.integration.installation.account.updating.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.installation.account.updating.enabled"}, havingValue = "true")
public class InstallationAccountUpdatingEventOutcomeConsumer extends AccountUpdatingEventOutcomeConsumer {

	public InstallationAccountUpdatingEventOutcomeConsumer(GroupNotificationClient groupNotificationClient,
                                                           ErrorNotificationProducer errorNotificationProducer, IntegrationHeadersUtil util,
                                                           AccountRepository accountRepository) {
		super(groupNotificationClient, errorNotificationProducer, util, accountRepository);
	}
}
