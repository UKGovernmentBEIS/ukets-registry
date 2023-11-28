package gov.uk.ets.registry.api.transaction.checks.allocation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckPendingAllocationTransactionTest {

    @InjectMocks
    CheckPendingAllocationTransaction check;

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void testPendingTransactionExists() {
        when(transactionRepository.countByTypeAndStatusNotIn(TransactionType.AllocateAllowances, TransactionStatus.getFinalStatuses())).thenReturn(1L);
        BusinessCheckContext context = new BusinessCheckContext();
        check.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testNoPendingTransactionExists() {
        when(transactionRepository.countByTypeAndStatusNotIn(TransactionType.AllocateAllowances,
            TransactionStatus.getFinalStatuses())).thenReturn(1L);
        BusinessCheckContext context = new BusinessCheckContext();
        check.execute(context);
        assertTrue(context.hasError());
    }

}