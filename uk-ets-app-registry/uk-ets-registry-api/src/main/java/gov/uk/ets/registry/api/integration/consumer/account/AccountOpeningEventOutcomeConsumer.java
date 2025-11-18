package gov.uk.ets.registry.api.integration.consumer.account;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.integration.AccountOpeningSuccessOutcomeNotification;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;

@Component
@Log4j2
@RequiredArgsConstructor
@KafkaListener(
    containerFactory = "accountOpeningOutcomeConsumerFactory",
    topics = {"${kafka.integration.installation.account.opening.response.topic}",
              "${kafka.integration.aviation.account.opening.response.topic}",
              "${kafka.integration.maritime.account.opening.response.topic}"}
)
@ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.account.opening.enabled"}, havingValue = "true")
public class AccountOpeningEventOutcomeConsumer {

    @Value("#{'${mail.integration.notification.address}'.split(';')}")
    private Set<String> integrationNotificationAddresses;

    private final GroupNotificationClient groupNotificationClient;

    private final ErrorNotificationProducer errorNotificationProducer;

    private final IntegrationHeadersUtil util;

    private final AccountRepository accountRepository;

    @KafkaHandler
    @Transactional
    public void processOutcome(AccountOpeningEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            String emitterId = Optional.of(outcome.getEvent())
                .map(AccountOpeningEvent::getAccountDetails)
                .map(AccountDetailsMessage::getEmitterId)
                .orElse(null);
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Emitter ID",
                emitterId, OperationEvent.OPEN_ACCOUNT, headers);
        } else {
            groupNotificationClient.emitGroupNotification(buildSuccessNotification(outcome, headers));
        }
    }

    private AccountOpeningSuccessOutcomeNotification buildSuccessNotification(AccountOpeningEventOutcome outcome,
                                                                              Map<String, Object> headers) {

        String correlationId = util.getCorrelationId(headers);

        Account newAccount = accountRepository.findByFullIdentifier(outcome.getAccountIdentifier())
            .orElseThrow(() -> new IllegalStateException("Account was not created"));

        return AccountOpeningSuccessOutcomeNotification.builder()
            .recipients(integrationNotificationAddresses)
            .accountFullIdentifier(outcome.getAccountIdentifier())
            .emitterId(newAccount.getCompliantEntity().getEmitterId())
            .accountName(newAccount.getAccountName())
            .accountType(newAccount.getAccountType())
            .integrationPoint(OperationEvent.OPEN_ACCOUNT.name())
            .sourceSystem(util.getSourceSystem(headers))
            .correlationId(correlationId)
            .date(new Date())
            .build();
    }
}
