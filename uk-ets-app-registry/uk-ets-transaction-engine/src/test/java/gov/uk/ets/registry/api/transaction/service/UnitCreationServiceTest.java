package gov.uk.ets.registry.api.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class UnitCreationServiceTest {

    @Mock
    private UnitBlockRepository unitBlockRepository;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private UnitMarkingService unitMarkingService;

    @InjectMocks
    private UnitCreationService unitCreationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTransactionBlock() {
        TransactionBlockSummary block = TransactionBlockSummary.builder()
            .startBlock(1L)
            .endBlock(10L)
            .type(UnitType.AAU)
            .originatingCountryCode("GB")
            .applicablePeriod(CommitmentPeriod.CP2)
            .originalPeriod(CommitmentPeriod.CP2)
            .build();

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .blocks(Collections.singletonList(block))
            .build();

        Mockito.when(unitBlockRepository.getNextAvailableSerialNumber()).thenReturn(100L);

        unitCreationService.generateTransactionBlock(transactionSummary, block, "GB");

        verify(transactionPersistenceService, times(1)).save(any());
        verify(unitBlockRepository, times(1)).updateNextAvailableSerialNumber(any());

        unitCreationService.generateTransactionBlocks(transactionSummary);
        verify(transactionPersistenceService, times(2)).save(any());

    }

    @Test
    void createUnitBlocks() {

        List<TransactionBlock> blocks = new ArrayList<>();

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.AAU);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setOriginatingCountryCode("GB");
        blocks.add(block);

        block = new TransactionBlock();
        block.setType(UnitType.RMU);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setOriginatingCountryCode("GB");
        blocks.add(block);

        unitCreationService.createUnitBlocks(1L, blocks);
        verify(transactionPersistenceService, times(2)).save(any());
    }

    @Test
    void createUnitBlocksEmpty() {
        unitCreationService.createUnitBlocks(1L, null);
        verify(transactionPersistenceService, never()).save(any());

        unitCreationService.createUnitBlocks(1L, new ArrayList<>());
        verify(transactionPersistenceService, never()).save(any());
    }

    @Test
    void createTransactionBlocks() {

        List<UnitBlock> blocks = new ArrayList<>();

        UnitBlock block = new UnitBlock();
        block.setType(UnitType.AAU);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setOriginatingCountryCode("GB");
        blocks.add(block);

        block = new UnitBlock();
        block.setType(UnitType.RMU);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setOriginatingCountryCode("GB");
        blocks.add(block);

        when(transactionPersistenceService.getUnitBlocks("GB123")).thenReturn(blocks);

        unitCreationService.createTransactionBlocks("GB123");

        verify(transactionPersistenceService, times(2)).save(any());

    }

    @Test
    void createTransactionBlocksEmpty() {
        when(transactionPersistenceService.getUnitBlocks("GB123")).thenReturn(new ArrayList<>());
        unitCreationService.createTransactionBlocks("GB123");
        verify(transactionPersistenceService, never()).save(any());
    }

}
