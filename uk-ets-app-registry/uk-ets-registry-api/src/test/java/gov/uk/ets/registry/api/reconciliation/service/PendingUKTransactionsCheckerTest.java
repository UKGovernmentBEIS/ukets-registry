package gov.uk.ets.registry.api.reconciliation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.reconciliation.service.PendingUKTransactionsChecker.PendingUKTransactionsException;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PendingUKTransactionsCheckerTest {

    @Mock
    private ETSTransactionService etsTransactionService;

    @Mock
    private RegistryLockProvider registryLockProvider;

    private PendingUKTransactionsChecker pendingUKTransactionsChecker;

    @BeforeEach
    void setup() {
        pendingUKTransactionsChecker = new PendingUKTransactionsChecker(etsTransactionService, registryLockProvider);
    }

    @DisplayName("PendingUKTransactionsChecker should throw a PendingUKTransactionsException when pending ets transactions exist")
    @Test
    void check() {
        // given
        given(etsTransactionService.countPendingETSTransactions()).willReturn(1L);
        //when then
        assertThrows(PendingUKTransactionsException.class, () -> pendingUKTransactionsChecker.check());

        // given
        given(etsTransactionService.countPendingETSTransactions()).willReturn(0L);
        // when then
        assertDoesNotThrow(() -> pendingUKTransactionsChecker.check());
    }
}