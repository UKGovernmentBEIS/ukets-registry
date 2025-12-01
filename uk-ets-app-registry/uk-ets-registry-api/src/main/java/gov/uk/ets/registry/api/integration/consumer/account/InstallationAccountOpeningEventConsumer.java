package gov.uk.ets.registry.api.integration.consumer.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.integration.service.account.AccountEventOpeningService;
import lombok.extern.log4j.Log4j2;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;

@Component
@Log4j2
@KafkaListener(
        containerFactory = "accountOpeningConsumerFactory",
        topics = {"${kafka.integration.installation.account.opening.request.topic}"}
    )
@ConditionalOnProperty(name = {"kafka.integration.installation.account.opening.enabled"}, havingValue = "true")
public class InstallationAccountOpeningEventConsumer extends AccountOpeningEventConsumer {
    
    @Value("${kafka.integration.installation.account.opening.response.topic}")
    private String installationAccountOpeningResponseTopic;
    
    public InstallationAccountOpeningEventConsumer(AccountEventOpeningService service,
                                                   @Qualifier("accountOpeningOutcomeKafkaTemplate")
                                                   KafkaTemplate<String, AccountOpeningEventOutcome> kafkaTemplate) {
          super(service, kafkaTemplate);
      }
    
    /**
     * Handles installation account creation.
     * @param event
     * @param headers
     */
    @KafkaHandler
    @Transactional
    public void consumeInstallation(AccountOpeningEvent event, @Headers Map<String, Object> headers) {
        processEvent(event, headers, installationAccountOpeningResponseTopic);
    }
}
