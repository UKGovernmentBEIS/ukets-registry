package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UnitMarkingServiceTest {

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    private UnitMarkingService unitMarkingService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        unitMarkingService = new UnitMarkingService(transactionPersistenceService);
    }

    @Test
    void markUnitsAsSubjectToSop() {
        MockitoAnnotations.initMocks(this);

        TransactionBlockSummary block = new TransactionBlockSummary();
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setType(UnitType.AAU);

        assertTrue(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.IssueOfAAUsAndRMUs, block));
        assertFalse(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.InternalTransfer, block));
        assertFalse(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.CarryOver_AAU, block));

        block = new TransactionBlockSummary();
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setType(UnitType.CER);
        assertFalse(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.IssueOfAAUsAndRMUs, block));

        block = new TransactionBlockSummary();
        block.setOriginalPeriod(CommitmentPeriod.CP1);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setType(UnitType.AAU);
        assertFalse(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.IssueOfAAUsAndRMUs, block));

        block = new TransactionBlockSummary();
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setApplicablePeriod(CommitmentPeriod.CP1);
        block.setType(UnitType.AAU);
        assertFalse(unitMarkingService.markUnitsAsSubjectToSop(TransactionType.IssueOfAAUsAndRMUs, block));

    }

    @Test
    void testChangeApplicablePeriod() {
        List<UnitBlock> blocks = new ArrayList<>();

        UnitBlock block = new UnitBlock();
        block.setType(UnitType.AAU);
        block.setOriginatingCountryCode("GB");
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        block.setOriginalPeriod(CommitmentPeriod.CP1);
        block.setApplicablePeriod(CommitmentPeriod.CP1);

        blocks.add(block);

        block = new UnitBlock();
        block.setType(UnitType.AAU);
        block.setOriginatingCountryCode("GB");
        block.setStartBlock(100L);
        block.setEndBlock(110L);
        block.setOriginalPeriod(CommitmentPeriod.CP1);
        block.setApplicablePeriod(CommitmentPeriod.CP1);

        List<UnitBlock> result = unitMarkingService.markApplicablePeriod(blocks, CommitmentPeriod.CP2);
        for (UnitBlock unitBlock : result) {
            assertEquals(CommitmentPeriod.CP1, unitBlock.getOriginalPeriod());
            assertEquals(CommitmentPeriod.CP2, unitBlock.getApplicablePeriod());
        }
    }
}
