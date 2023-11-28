package uk.gov.ets.transaction.log.checks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

class CheckSerialNumbersTest {

    @Mock
    UnitBlockRepository unitBlockRepository;

    @InjectMocks
    CheckSerialNumbers checkSerialNumbers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIssuanceTransaction() {
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setType(TransactionType.IssueAllowances);
                setBlocks(Arrays.asList(new TransactionBlock() {{
                    setStartBlock(1_000_000L);
                    setEndBlock(1_999_999L);
                    setType(UnitType.ALLOWANCE);
                }}));
            }});
        }};
        checkSerialNumbers.execute(context);

        verify(unitBlockRepository, times(0))
            .findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(any(), any(), any());

        assertFalse(context.hasError());
    }

    @Test
    void testSerialNumbersAllBlocksDoNotExist() {
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setType(TransactionType.TransferAllowances);
                setBlocks(Arrays.asList(
                    new TransactionBlock() {{
                        setStartBlock(1_000_000L);
                        setEndBlock(1_999_999L);
                        setType(UnitType.ALLOWANCE);
                    }},
                    new TransactionBlock() {{
                        setStartBlock(2_000_000L);
                        setEndBlock(2_000_009L);
                        setType(UnitType.ALLOWANCE);
                    }}));
            }});
        }};

        when(unitBlockRepository.findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
             any(), any(), any())).thenReturn(Optional.empty());

        checkSerialNumbers.execute(context);

        verify(unitBlockRepository, times(2))
            .findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(any(), any(), any());

        assertTrue(context.hasError());
    }

    @Test
    void testSerialNumbersOneBlockDoesNotExist() {
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setTransferringAccountIdentifier(1L);
                setType(TransactionType.TransferAllowances);
                setBlocks(Arrays.asList(
                    new TransactionBlock() {{
                        setStartBlock(1_000_000L);
                        setEndBlock(1_999_999L);
                        setType(UnitType.ALLOWANCE);
                    }},
                    new TransactionBlock() {{
                        setStartBlock(2_000_000L);
                        setEndBlock(2_000_009L);
                        setType(UnitType.ALLOWANCE);
                    }}));
            }});
        }};

        when(unitBlockRepository.findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
            1_000_000L, 1_999_999L, 1L)).thenReturn(Optional.empty());

        when(unitBlockRepository.findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
            2_000_000L, 2_000_009L, 1L)).thenReturn(Optional.of(new UnitBlock()));

        checkSerialNumbers.execute(context);

        verify(unitBlockRepository, times(2))
            .findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(any(), any(), any());

        assertTrue(context.hasError());
    }

    @Test
    void testSerialNumbersAllBlocksExist() {
        BusinessCheckContext context = new BusinessCheckContext() {{
            setTransaction(new TransactionNotification() {{
                setQuantity(10L);
                setTransferringAccountIdentifier(1L);
                setType(TransactionType.TransferAllowances);
                setBlocks(Arrays.asList(
                    new TransactionBlock() {{
                        setStartBlock(1_000_000L);
                        setEndBlock(1_999_999L);
                        setType(UnitType.ALLOWANCE);
                    }},
                    new TransactionBlock() {{
                        setStartBlock(2_000_000L);
                        setEndBlock(2_000_009L);
                        setType(UnitType.ALLOWANCE);
                    }}));
            }});
        }};

        when(unitBlockRepository.findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
            1_000_000L, 1_999_999L, 1L)).thenReturn(Optional.of(new UnitBlock()));

        when(unitBlockRepository.findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
            2_000_000L, 2_000_009L, 1L)).thenReturn(Optional.of(new UnitBlock()));

        checkSerialNumbers.execute(context);

        verify(unitBlockRepository, times(2))
            .findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(any(), any(), any());

        assertFalse(context.hasError());
    }

}