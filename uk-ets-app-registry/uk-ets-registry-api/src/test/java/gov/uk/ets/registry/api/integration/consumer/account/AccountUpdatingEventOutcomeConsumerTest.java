package gov.uk.ets.registry.api.integration.consumer.account;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.consumer.account.update.AccountUpdatingEventOutcomeConsumer;
import gov.uk.ets.registry.api.integration.consumer.account.update.InstallationAccountUpdatingEventOutcomeConsumer;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.integration.AccountUpdatingSuccessOutcomeNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.support.KafkaHeaders;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountUpdatingEventOutcomeConsumerTest {

    @Mock
    private GroupNotificationClient groupNotificationClient;

    @Mock
    private ErrorNotificationProducer errorNotificationProducer;

    @Mock
    private IntegrationHeadersUtil util;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private InstallationAccountUpdatingEventOutcomeConsumer consumer;

    private Map<String, Object> headers;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        headers = Map.of(KafkaHeaders.CORRELATION_ID, "corr-123");
        var field = AccountUpdatingEventOutcomeConsumer.class.getDeclaredField("integrationNotificationAddresses");
        field.setAccessible(true);
        field.set(consumer, Set.of("notify@test.com"));
    }

    @Test
    void shouldSendErrorNotificationWhenOutcomeIsError() {
        // given
        AccountUpdatingEvent event = new AccountUpdatingEvent();
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId("REG-001");
        event.setAccountDetails(details);

        AccountUpdatingEventOutcome outcome = AccountUpdatingEventOutcome.builder()
                .errors(List.of())
                .accountIdentifier("ACC-123")
                .event(event)
                .outcome(IntegrationEventOutcome.ERROR)
                .isModifiedInRegistry(false)
                .build();

        consumer.processOutcome(outcome, headers);
        verify(errorNotificationProducer).sendNotifications(
                eq(event),
                anyList(),
                eq("Registry ID"),
                eq("REG-001"),
                eq(OperationEvent.UPDATE_ACCOUNT_DETAILS),
                eq(headers)
        );
        verifyNoInteractions(groupNotificationClient);
    }

    @Test
    void shouldEmitGroupNotificationWhenOutcomeIsSuccess() {
        Account account = new Account();
        account.setAccountName("Test Account");
        account.setAccountType("MARITIME");
        var compliantEntity = new MaritimeOperator();
        compliantEntity.setEmitterId("EM-999");
        account.setCompliantEntity(compliantEntity);

        when(accountRepository.findByFullIdentifier("FULL-777123"))
                .thenReturn(Optional.of(account));
        when(util.getCorrelationId(headers)).thenReturn("corr-123");
        when(util.getSourceSystem(headers)).thenReturn(SourceSystem.MARITIME);

        AccountUpdatingEventOutcome outcome = AccountUpdatingEventOutcome.builder()
                .errors(List.of())
                .accountIdentifier( "FULL-777123")
                .event(new AccountUpdatingEvent())
                .outcome(IntegrationEventOutcome.SUCCESS)
                .isModifiedInRegistry(true)
                .build();

        consumer.processOutcome(outcome, headers);
        verify(groupNotificationClient).emitGroupNotification(
                isA(AccountUpdatingSuccessOutcomeNotification.class)
        );
        verifyNoInteractions(errorNotificationProducer);
    }

    @Test
    void shouldThrowWhenAccountNotFoundForSuccessOutcome() {
        when(accountRepository.findByFullIdentifier("125404")).thenReturn(Optional.empty());

        AccountUpdatingEventOutcome outcome = AccountUpdatingEventOutcome.builder()
                .errors(List.of())
                .accountIdentifier("125404")
                .event(new AccountUpdatingEvent())
                .outcome(IntegrationEventOutcome.SUCCESS)
                .isModifiedInRegistry(true)
                .build();

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> consumer.processOutcome(outcome, headers)
        );

        assertEquals("Account was not updated", ex.getMessage());
        verifyNoInteractions(groupNotificationClient, errorNotificationProducer);
    }
}
