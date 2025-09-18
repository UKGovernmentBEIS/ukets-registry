package gov.uk.ets.registry.api.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddAccountInfoCommand;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddTransactionCommand;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.transaction.domain.QTransactionResponse;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryCode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.messaging.UKTLOutgoingMessageService;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountBalanceService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionStarter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;

import jakarta.persistence.EntityManager;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.transaction.annotation.Transactional;

@EmbeddedKafka(topics = {
    "registry.originating.notification.topic",
    "proposal.notification.in",
    "itl.notices.in.topic",
    "domain.event.topic",
    "group.notification.topic",
    "registry.originating.reconciliation.question.topic",
    "registry.originating.transaction.question.topic",
    "txlog.originating.reconciliation.answer.topic"
},
    brokerPropertiesLocation = "classpath:integration-test-application.properties",
    brokerProperties = "auto.create.topics.enable=false",
    count = 3,
    ports = {0, 0, 0}
)
public class TransactionsStarterIntegrationTest extends BaseIntegrationTest {

    @SpyBean
    UKTLOutgoingMessageService uktlOutgoingMessageService;

    @Autowired
    private TransactionStarter delayedTransactionStarter;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @SpyBean
    NotificationService notificationService;

    @MockBean
    TransactionAccountService transactionAccountService;

    @MockBean
    TransactionAccountBalanceService transactionAccountBalanceService;
    
    @SpyBean
    AccountService accountService;

    private TransactionModelTestHelper transactionModelTestHelper;

    @BeforeEach
    void setup() {
        transactionModelTestHelper = new TransactionModelTestHelper(entityManager);
    }

    @Transactional
    @Test
    void startDelayedTransactionsWhenKafkaIsDown() throws Exception {
        // given
        TransactionCommand command = TransactionCommand.builder()
            .started(Date.from(LocalDateTime.now().minusHours(25).atZone(ZoneId.systemDefault()).toInstant()))
            .identifier("UK12323")
            .status(TransactionStatus.DELAYED)
            .executionDate(LocalDateTime.now().minusDays(1))
            .build();

        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setAccountStatus(AccountStatus.OPEN);
        Transaction transaction = addTransaction(command);
        doThrow(new RuntimeException()).when(uktlOutgoingMessageService).sendProposalRequest(any());
        doReturn(1L).when(notificationService).findRequestIdForTransaction(command.identifier);
        when(transactionAccountService.populateAcquiringAccount(any(TransactionSummary.class))).thenReturn(accountSummary);
        doReturn(new HashSet<String>()).when(notificationService)
            .findEmailsOfArsByAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier(), true, true);
        doReturn(new HashSet<String>()).when(notificationService)
            .findEmailsOfArsByAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier(), true,
                true);
        doReturn(1L).when(notificationService).findRequestIdForTransaction(command.identifier);
        doReturn(getAccount()).when(accountService)
            .getAccount(transaction.getTransferringAccount().getAccountIdentifier());
        doNothing().when(transactionAccountBalanceService).updateTransactionAccountBalances(any());
        // when
        delayedTransactionStarter.startDelayedTransactions();

        //then
        thenTheTransactionShouldFail(command);
    }


    @Test
    @Transactional
    void checkForStoppedTransactions() throws Exception {
        // given
        TransactionCommand command = TransactionCommand.builder()
            .started(Date.from(LocalDateTime.now().minusHours(25).atZone(ZoneId.systemDefault()).toInstant()))
            .identifier("UK12323")
            .status(TransactionStatus.PROPOSED)
            .build();

        Transaction transaction = addTransaction(command);
        doThrow(new RuntimeException()).when(uktlOutgoingMessageService).sendProposalRequest(any());
        doReturn(1L).when(notificationService).findRequestIdForTransaction(command.identifier);

        doReturn(new HashSet<String>()).when(notificationService)
            .findEmailsOfArsByAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier(), true, true);
        doReturn(new HashSet<String>()).when(notificationService)
            .findEmailsOfArsByAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier(), true,
                true);
        doReturn(1L).when(notificationService).findRequestIdForTransaction(command.identifier);
        doReturn(getAccount()).when(accountService)
            .getAccount(transaction.getTransferringAccount().getAccountIdentifier());
        doNothing().when(transactionAccountBalanceService).updateTransactionAccountBalances(any());
        
        // when
        delayedTransactionStarter.checkForStoppedTransactions(24);

        //then
        thenTheTransactionShouldFail(command);
    }

    private void thenTheTransactionShouldFail(TransactionCommand command) {
        TransactionResponse response = new JPAQuery<TransactionResponse>(entityManager)
            .from(QTransactionResponse.transactionResponse)
            .where(QTransactionResponse.transactionResponse.transaction.identifier
                .eq(command.identifier)).fetchOne();
        assertNotNull(response);
        assertEquals(-1, response.getErrorCode());
        Transaction failedTransaction = transactionRepository.findByIdentifier(command.identifier);
        assertEquals(TransactionStatus.FAILED, failedTransaction.getStatus());
    }

    private Transaction addTransaction(TransactionCommand command) throws Exception {
        AddTransactionCommand addTransactionCommand = AddTransactionCommand
            .builder()
            .addAcquiringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("acquiring-test-account-key")
                .accountRegistryCode("UK")
                .accountIdentifier(1000L)
                .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
                .build())
            .addTransferringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("transferring-test-account-key")
                .accountIdentifier(2000L)
                .accountRegistryCode("UK")
                .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
                .build())
            .identifier(command.identifier)
            .quantity(20L)
            .status(command.status)
            .startDate(command.started)
            .executionDate(command.executionDate)
            .type(TransactionType.CentralTransferAllowances)
            .unitType(UnitType.AAU)
            .lastUpdate("12/03/2020 13:23")
            .build();
        Transaction transaction = transactionModelTestHelper.addTransaction(addTransactionCommand);
        return transaction;
    }

    @Builder
    static class TransactionCommand {
        String identifier;
        Date started;
        LocalDateTime executionDate;
        TransactionStatus status;
    }

    Account getAccount() {
        Account account = new Account();
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(false);
        account.setFullIdentifier("UK-100-1000004-2-22");
        account.setRegistryCode(RegistryCode.UK.getCode());
        return account;
    }

}
