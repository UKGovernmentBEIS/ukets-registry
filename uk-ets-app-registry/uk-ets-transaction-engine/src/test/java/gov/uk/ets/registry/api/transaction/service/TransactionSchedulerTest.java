package gov.uk.ets.registry.api.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.List;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionSchedulerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionAccountService transactionAccountService;

    private TransactionScheduler transactionScheduler;

    @BeforeEach
    void setUp() {
        LockAssert.TestHelper.makeAllAssertsPass(true);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void noDelayedTransactionsFound() {
        transactionScheduler = new TransactionScheduler(new TransactionStarter(transactionRepository,
                                                                               transactionService,
                                                                               transactionAccountService), 24);
        when(transactionRepository.findByStatusEqualsAndExecutionDateBefore(any(), any())).thenReturn(new ArrayList<>());
        transactionScheduler.launchDelayedTransactions();
        verify(transactionService, times(0)).startTransaction(any());

        when(transactionRepository.findByStatusEqualsAndExecutionDateBefore(any(), any())).thenReturn(null);
        transactionScheduler.launchDelayedTransactions();
        verify(transactionService, times(0)).startTransaction(any());
    }

    @Test
    void delayedTransactionsProcessed() {
        transactionScheduler = new TransactionScheduler(new TransactionStarter(transactionRepository,
                                                                               transactionService,
                                                                               transactionAccountService), 24);

        List<Transaction> transactions = new ArrayList<>();
        AccountBasicInfo accountBasicInfo = new AccountBasicInfo();
        accountBasicInfo.setAccountIdentifier(10000063L);
        accountBasicInfo.setAccountFullIdentifier("UK-100-10000063-0-37");
        Transaction transaction = new Transaction();
        transaction.setIdentifier("UK100175");
        transaction.setType(TransactionType.TransferAllowances);
        transaction.setAcquiringAccount(accountBasicInfo);
        transactions.add(transaction);

        transaction = new Transaction();
        accountBasicInfo.setAccountFullIdentifier("UK-100-10000063-0-37");
        transaction.setIdentifier("UK100174");
        transaction.setType(TransactionType.TransferAllowances);
        transaction.setAcquiringAccount(accountBasicInfo);
        transactions.add(transaction);

        when(transactionRepository.findByStatusEqualsAndExecutionDateBefore(any(), any())).thenReturn(transactions);
        when(transactionAccountService.populateAcquiringAccountStatus(any(String.class))).thenReturn(AccountStatus.OPEN);
        transactionScheduler.launchDelayedTransactions();
        verify(transactionService, times(transactions.size())).startTransaction(any());
    }
}