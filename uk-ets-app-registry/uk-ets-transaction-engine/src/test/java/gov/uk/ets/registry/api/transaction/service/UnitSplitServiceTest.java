package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UnitSplitServiceTest {

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    private UnitSplitService unitSplitService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        unitSplitService = new UnitSplitService(transactionPersistenceService);
    }

    @Test
    public void splitEmptyBlock() {
        assertThrows(TransactionExecutionException.class, () -> {
            unitSplitService.split(null, true, 1000L);
        });

        assertThrows(TransactionExecutionException.class, () -> {
            unitSplitService.split(null, false, 1000L);
        });

    }

    @Test
    public void splitInvalidArguments() {
        UnitBlock block = new UnitBlock();
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        assertNull(unitSplitService.split(block, true, 1L));
        assertThrows(TransactionExecutionException.class, () -> {
            unitSplitService.split(block, true, 0L);
        });
        verify(transactionPersistenceService, never()).saveAndFlush(any());
        verify(transactionPersistenceService, never()).save(any());


        assertNull(unitSplitService.split(block, false, 10L));
        assertThrows(TransactionExecutionException.class, () -> {
            unitSplitService.split(block, false, 11L);
        });
        verify(transactionPersistenceService, never()).saveAndFlush(any());
        verify(transactionPersistenceService, never()).save(any());

    }

    @Test
    public void splitSuccess() {
        UnitBlock block = new UnitBlock();
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        unitSplitService.split(block, true, 8L);
        verify(transactionPersistenceService, times(1)).saveAndFlush(any());
        verify(transactionPersistenceService, times(1)).save(any());

        unitSplitService.split(block, false, 2L);
        verify(transactionPersistenceService, times(2)).saveAndFlush(any());
        verify(transactionPersistenceService, times(2)).save(any());

    }


}