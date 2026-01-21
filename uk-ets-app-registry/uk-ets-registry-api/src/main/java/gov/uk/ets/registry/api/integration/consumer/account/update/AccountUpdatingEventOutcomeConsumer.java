package gov.uk.ets.registry.api.integration.consumer.account.update;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.integration.AccountUpdatingSuccessOutcomeNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
public abstract class AccountUpdatingEventOutcomeConsumer {

    @Value("#{'${mail.integration.notification.address}'.split(';')}")
    private Set<String> integrationNotificationAddresses;

    private final GroupNotificationClient groupNotificationClient;

    private final ErrorNotificationProducer errorNotificationProducer;

    private final IntegrationHeadersUtil util;

    private final AccountRepository accountRepository;

    @KafkaHandler
    @Transactional
    public void processOutcome(AccountUpdatingEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            String registryId = Optional.of(outcome.getEvent())
                    .map(AccountUpdatingEvent::getAccountDetails)
                    .map(UpdateAccountDetailsMessage::getRegistryId)
                    .orElse(null);
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Registry ID",
                    registryId, OperationEvent.UPDATE_ACCOUNT_DETAILS, headers);
        } else {
            if(outcome.getIsModifiedInRegistry()) {
                groupNotificationClient.emitGroupNotification(buildSuccessNotification(outcome, headers));
            }
        }
    }

    private AccountUpdatingSuccessOutcomeNotification buildSuccessNotification(AccountUpdatingEventOutcome outcome,
                                                                                   Map<String, Object> headers) {

        String correlationId = util.getCorrelationId(headers);

        Account newAccount = accountRepository.findByFullIdentifier(outcome.getAccountIdentifier())
                .orElseThrow(() -> new IllegalStateException("Account was not updated"));

        return AccountUpdatingSuccessOutcomeNotification.builder()
                .recipients(integrationNotificationAddresses)
                .accountFullIdentifier(outcome.getAccountIdentifier())
                .emitterId(newAccount.getCompliantEntity().getEmitterId() != null
                        ? newAccount.getCompliantEntity().getEmitterId()
                        : "N/A")
                .accountName(newAccount.getAccountName())
                .accountType(newAccount.getAccountType())
                .integrationPoint(OperationEvent.UPDATE_ACCOUNT_DETAILS.name())
                .sourceSystem(util.getSourceSystem(headers))
                .correlationId(correlationId)
                .date(new Date())
                .build();
    }
}
