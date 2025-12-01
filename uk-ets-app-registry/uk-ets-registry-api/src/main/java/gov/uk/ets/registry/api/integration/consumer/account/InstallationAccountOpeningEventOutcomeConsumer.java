package gov.uk.ets.registry.api.integration.consumer.account;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import lombok.extern.log4j.Log4j2;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "accountOpeningOutcomeConsumerFactory",
        topics = {"${kafka.integration.installation.account.opening.response.topic}"}
    )
@ConditionalOnProperty(name = {"kafka.integration.installation.account.opening.enabled"}, havingValue = "true")
public class InstallationAccountOpeningEventOutcomeConsumer extends AccountOpeningEventOutcomeConsumer {

	public InstallationAccountOpeningEventOutcomeConsumer(GroupNotificationClient groupNotificationClient,
			ErrorNotificationProducer errorNotificationProducer, IntegrationHeadersUtil util,
			AccountRepository accountRepository) {
		super(groupNotificationClient, errorNotificationProducer, util, accountRepository);
	}

	
	  /**
	  * Handles installation account creation outcome.
	  * @param outcome
	  * @param headers
	  */
	 @KafkaHandler
	 @Transactional
	 public void consumeInstallation(AccountOpeningEventOutcome outcome, @Headers Map<String, Object> headers) {
	     processOutcome(outcome, headers);
	 }
}
