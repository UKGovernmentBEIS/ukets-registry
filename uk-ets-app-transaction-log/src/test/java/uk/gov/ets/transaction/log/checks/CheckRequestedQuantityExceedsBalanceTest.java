package uk.gov.ets.transaction.log.checks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

class CheckRequestedQuantityExceedsBalanceTest {

    @Mock
    UnitBlockRepository unitBlockRepository;

    @InjectMocks
    CheckRequestedQuantityExceedsBalance checkRequestedQuantityExceedsBalance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBalanceNotAvailable() {
        when(unitBlockRepository.calculateAvailableQuantity(any(), any())).thenReturn(0L);
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setType(TransactionType.CentralTransferAllowances);
            }});
        }};
        checkRequestedQuantityExceedsBalance.execute(context);
        assertTrue(context.hasError());

        when(unitBlockRepository.calculateAvailableQuantity(any(), any())).thenReturn(9L);
        checkRequestedQuantityExceedsBalance.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testBalanceAvailable() {
        when(unitBlockRepository.calculateAvailableQuantity(any(), any())).thenReturn(10L);
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setType(TransactionType.TransferAllowances);
            }});
        }};
        checkRequestedQuantityExceedsBalance.execute(context);
        assertFalse(context.hasError());

        when(unitBlockRepository.calculateAvailableQuantity(any(), any())).thenReturn(12L);
        checkRequestedQuantityExceedsBalance.execute(context);
        assertFalse(context.hasError());
    }

    @Test
    void testTransactionIssuance() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(10L);
            setType(TransactionType.IssueAllowances);
        }});

        checkRequestedQuantityExceedsBalance.execute(context);
        assertFalse(context.hasError());
    }

}