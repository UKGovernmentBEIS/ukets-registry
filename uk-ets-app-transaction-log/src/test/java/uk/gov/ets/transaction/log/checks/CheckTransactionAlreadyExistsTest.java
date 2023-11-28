package uk.gov.ets.transaction.log.checks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.TransactionRepository;

class CheckTransactionAlreadyExistsTest {

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    CheckTransactionAlreadyExists checkTransactionAlreadyExists;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCheck() {
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setIdentifier("UK10000001");
                setQuantity(10L);
                setType(TransactionType.IssueAllowances);
                setBlocks(Arrays.asList(new TransactionBlock() {{
                    setStartBlock(1_000_000L);
                    setEndBlock(1_999_999L);
                    setType(UnitType.ALLOWANCE);
                }}));
            }});
        }};

        when(transactionRepository.findByIdentifier(any())).thenReturn(Optional.empty());
        checkTransactionAlreadyExists.execute(context);
        assertFalse(context.hasError());

        when(transactionRepository.findByIdentifier(any())).thenReturn(Optional.of(new Transaction()));
        checkTransactionAlreadyExists.execute(context);
        assertTrue(context.hasError());
    }
}
