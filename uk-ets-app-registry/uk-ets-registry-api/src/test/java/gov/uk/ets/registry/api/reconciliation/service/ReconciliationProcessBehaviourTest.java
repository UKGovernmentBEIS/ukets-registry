package gov.uk.ets.registry.api.reconciliation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.querydsl.core.types.Predicate;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountStatusService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.messaging.UKTLOutboundAdapter;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationFailedEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReconciliationProcessBehaviourTest {

    ProcessReconciliationService processReconciliationService;

    @Mock
    ETSTransactionService etsTransactionService;

    @Mock
    PendingUKTransactionsChecker pendingUKTransactionsChecker;

    @Mock
    UKTLOutboundAdapter uktlOutboundAdapter;

    @Mock
    ReconciliationService reconciliationService;


    AccountStatusService accountStatusService;

    @Mock
    EventService eventService;

    @Mock
    AccountRepository accountRepository;

    @Captor
    ArgumentCaptor<Set<Long>> accountIdentifiersCaptor;


    @Captor
    ArgumentCaptor<ReconciliationSummary> reconciliationSummaryArgumentCaptor;


    @BeforeEach
    void setUp() {
        accountStatusService = new AccountStatusService(accountRepository, eventService);
        processReconciliationService = new ProcessReconciliationService(pendingUKTransactionsChecker, uktlOutboundAdapter,
            new ReconciliationActionService(reconciliationService, accountRepository, accountStatusService));
    }

    @DisplayName("Reconciliation starts by creating reconciliation, getting the unit blocks totals and finishing by sending message to UKTL")
    @Test
    void initiate() {
        // given
        long identifier = 1L;
        long accountIdentifier = 1001L;
        ReconciliationEntrySummary reconciliationEntrySummary = new ReconciliationEntrySummary(accountIdentifier, UnitType.AAU, 200L);
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(identifier);
        given(reconciliationService.createReconciliation(any())).willReturn(reconciliation);
        Set<Long> accountIdentifiers = Set.of(accountIdentifier);

        given(accountRepository.findAll(any(Predicate.class))).willReturn(
            accountIdentifiers.stream().map(id -> {
                Account account = new Account();
                account.setIdentifier(id);
                return account;
            }).collect(Collectors.toList()));

        given(reconciliationService.getReconciliationEntrySummaries(accountIdentifiers)).willReturn(List.of(reconciliationEntrySummary));

        // when
        processReconciliationService.initiate(new Date());

        // then
        then(reconciliationService).should(times(1)).createReconciliation(any());
        then(reconciliationService).should(times(1)).getReconciliationEntrySummaries(accountIdentifiersCaptor.capture());
        assertEquals(accountIdentifiers, accountIdentifiersCaptor.getValue());
        then(uktlOutboundAdapter).should(times(1)).sendMessage(reconciliationSummaryArgumentCaptor.capture());
        ReconciliationSummary message = reconciliationSummaryArgumentCaptor.getValue();
        assertEquals(identifier, message.getIdentifier());
        assertEquals(ReconciliationStatus.INITIATED, message.getStatus());
        assertEquals(List.of(reconciliationEntrySummary), message.getEntries());
    }

    @DisplayName("When an error occurs during the creation of the new Reconciliation object then no message should be sent to UKTL")
    @Test
    void testErrorDuringReconciliationCreation() {
        // given
        long identifier = 1L;
        given(reconciliationService.createReconciliation(any())).willThrow(new RuntimeException("test error"));

        //when
        try {
            processReconciliationService.initiate(new Date());
        } catch(Exception exception) {
            //;
        }

        // then
        then(reconciliationService).should(times(1)).createReconciliation(any());
        then(uktlOutboundAdapter).should(times(0)).sendMessage(any());
    }

    @DisplayName("When an error occurs during initiation and after the reconciliation has been stored to database then this reconciliation should be deleted from database")
    @Test
    void testErrorAfterReconciliationCreation() {
        // given
        long identifier = 1L;
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(identifier);
        given(reconciliationService.createReconciliation(any())).willReturn(reconciliation);
        doThrow(new RuntimeException()).when(uktlOutboundAdapter).sendMessage(any());
        // when
        try { processReconciliationService.initiate(new Date()); } catch (Exception e) { };
        // then
        then(reconciliationService).should(times(1)).createReconciliation(any());
        //then(reconciliationService).should(times(1)).getReconciliationEntrySummaries();
        then(uktlOutboundAdapter).should(times(1)).sendMessage(any());
        //then(reconciliationService).should(times(1)).deleteReconciliation(identifier);
    }

    @Test
    void testProcessFailedEntriesEmptyInput() {
        processReconciliationService.completeReconciliation(new ReconciliationSummary());
        verify(accountRepository, times(0)).save(any());
        verify(accountRepository, times(0)).findByIdentifier(any());

        ReconciliationSummary reconciliation = new ReconciliationSummary();
        reconciliation.setFailedEntries(new ArrayList<>());
        processReconciliationService.completeReconciliation(reconciliation);
        verify(accountRepository, times(0)).save(any());
        verify(accountRepository, times(0)).findByIdentifier(any());
    }

    @Test
    void testProcessFailedEntries() {
        ReconciliationSummary reconciliation = new ReconciliationSummary();
        reconciliation.setStatus(ReconciliationStatus.INCONSISTENT);
        reconciliation.setIdentifier(1000L);
        reconciliation.setFailedEntries(new ArrayList<>(){{

            add(new ReconciliationFailedEntrySummary() {{
                setAccountIdentifier(1L);
                setTotalInRegistry(100L);
                setTotalInTransactionLog(200L);
                setUnitType(UnitType.ALLOWANCE);
            }});

            add(new ReconciliationFailedEntrySummary() {{
                setAccountIdentifier(2L);
                setTotalInRegistry(1_000_000L);
                setTotalInTransactionLog(1_000_001L);
                setUnitType(UnitType.ALLOWANCE);
            }});
        }});

        Account account1 = new Account();
        account1.setIdentifier(1L);
        account1.setAccountStatus(AccountStatus.OPEN);
        Mockito.when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account1));

        Mockito.when(accountRepository.findByIdentifier(2L)).thenReturn(Optional.empty());

        processReconciliationService.completeReconciliation(reconciliation);

        verify(accountRepository, times(1)).save(any());
        verify(accountRepository, times(1)).findByIdentifier(1L);
        verify(accountRepository, times(1)).findByIdentifier(2L);
    }

}