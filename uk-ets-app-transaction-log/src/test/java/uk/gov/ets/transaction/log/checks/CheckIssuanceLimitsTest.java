package uk.gov.ets.transaction.log.checks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.service.LimitService;

class CheckIssuanceLimitsTest {

    @Mock
    LimitService limitService;

    @InjectMocks
    CheckIssuanceLimits checkIssuanceLimits;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testPhaseNotDefined() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.IssueAllowances);
        }});

        when(limitService.getIssuanceLimit()).thenReturn(null);

        checkIssuanceLimits.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testIssuanceLimitNotSet() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.IssueAllowances);
        }});

        when(limitService.getIssuanceLimit()).thenReturn(0L);

        checkIssuanceLimits.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testIssuanceLimitNotEnough() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.IssueAllowances);
        }});

        when(limitService.getIssuanceLimit()).thenReturn(50L);

        checkIssuanceLimits.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testIssuanceLimitConsumed() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.IssueAllowances);
        }});

        when(limitService.getIssuanceLimit()).thenReturn(0L);

        checkIssuanceLimits.execute(context);
        assertTrue(context.hasError());
    }


    @Test
    void testIssuanceLimitEnough() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.IssueAllowances);
        }});

        when(limitService.getIssuanceLimit()).thenReturn(9000L);

        checkIssuanceLimits.execute(context);
        assertFalse(context.hasError());
    }

    @Test
    void testTransactionNotIssuance() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setQuantity(100L);
            setType(TransactionType.AuctionDeliveryAllowances);
        }});

        checkIssuanceLimits.execute(context);
        assertFalse(context.hasError());
    }

}