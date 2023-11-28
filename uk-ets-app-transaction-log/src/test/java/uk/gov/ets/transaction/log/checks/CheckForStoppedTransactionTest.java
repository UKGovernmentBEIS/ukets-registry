package uk.gov.ets.transaction.log.checks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

class CheckForStoppedTransactionTest {

    private final Integer maxPeriodInHours = 24;
    CheckForStoppedTransaction checkForStoppedTransaction;

    @BeforeEach
    void setUp() {
        openMocks(this);
        checkForStoppedTransaction = new CheckForStoppedTransaction(maxPeriodInHours);
    }

    @Test
    void test_TransactionIsLate() {
        Date transactionStartDate = Date.from(LocalDateTime.now().minusHours(maxPeriodInHours + 10)
                .atZone(ZoneId.systemDefault()).toInstant());

        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification() {{
            setStarted(transactionStartDate);
        }});

        checkForStoppedTransaction.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void test_TransactionNotLate() {
        Date transactionStartDate = Date.from(LocalDateTime.now().minusHours(maxPeriodInHours - 10)
                .atZone(ZoneId.systemDefault()).toInstant());

        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification() {{
            setStarted(transactionStartDate);
        }});

        checkForStoppedTransaction.execute(context);
        assertFalse(context.hasError());
    }
}
