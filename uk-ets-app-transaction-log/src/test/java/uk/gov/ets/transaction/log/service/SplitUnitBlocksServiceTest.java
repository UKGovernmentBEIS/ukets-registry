package uk.gov.ets.transaction.log.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;


@DisplayName("Testing split block related service methods")
public class SplitUnitBlocksServiceTest {

    private SplitUnitBlocksService splitUnitBlocksService;
    @Mock
    private UnitBlockRepository unitBlockRepository;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        splitUnitBlocksService = new SplitUnitBlocksService(unitBlockRepository);
    }
    
    @Test
    @DisplayName("Non overlapping blocks should not split , expected to succeed")
    void testNonOverlappingUnitBlocks() {
        TransactionNotification proposal = new TransactionNotification();
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        List<UnitBlock> dbUnitBlocks = new ArrayList<>();
        Mockito.when(unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(1200L,1250L)).thenReturn(dbUnitBlocks);
        
        assertEquals(0L, splitUnitBlocksService.splitUnitBlocks(proposal));
    }
    
    @Test
    @DisplayName("DB Block start early should split , expected to succeed")
    void testDBBlockStartEarly() {
        TransactionNotification proposal = new TransactionNotification();
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        List<UnitBlock> dbUnitBlocks = new ArrayList<>();
        UnitBlock dbBlock_1 = new UnitBlock();
        dbBlock_1.setStartBlock(1150L);
        dbBlock_1.setEndBlock(1250L);
        dbUnitBlocks.add(dbBlock_1);
        Mockito.when(unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(1200L,1250L)).thenReturn(dbUnitBlocks);
        Mockito.when(unitBlockRepository.saveAndFlush(Mockito.any(UnitBlock.class))).thenAnswer(i -> i.getArguments()[0]);
        
        assertEquals(1L, splitUnitBlocksService.splitUnitBlocks(proposal));
        assertEquals(1200L, dbBlock_1.getStartBlock());
        assertEquals(1250L, dbBlock_1.getEndBlock());
    }
    
    @Test
    @DisplayName("DB Block ends late should split , expected to succeed")
    void testDBBlockEndsLate() {
        TransactionNotification proposal = new TransactionNotification();
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1100L);
        transactionBlock_1.setEndBlock(1325L);
        transactionBlock_1.setYear(2021);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        List<UnitBlock> dbUnitBlocks = new ArrayList<>();
        UnitBlock dbBlock_1 = new UnitBlock();
        dbBlock_1.setStartBlock(1100L);
        dbBlock_1.setEndBlock(1400L);
        dbUnitBlocks.add(dbBlock_1);
        Mockito.when(unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(1100L,1325L)).thenReturn(dbUnitBlocks);
        Mockito.when(unitBlockRepository.saveAndFlush(Mockito.any(UnitBlock.class))).thenAnswer(i -> i.getArguments()[0]);
        
        assertEquals(1L, splitUnitBlocksService.splitUnitBlocks(proposal));
        assertEquals(1100L, dbBlock_1.getStartBlock());
        assertEquals(1325L, dbBlock_1.getEndBlock());
    }
    
    @Test
    @DisplayName("DB Block start early should split and DB Block ends late should split, expected to succeed")
    void testtestDBBlockStartEarlyAndDBBlockEndsLate() {
        TransactionNotification proposal = new TransactionNotification();
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1100L);
        transactionBlock_1.setEndBlock(1325L);
        transactionBlock_1.setYear(2021);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        List<UnitBlock> dbUnitBlocks = new ArrayList<>();
        UnitBlock dbBlock_1 = new UnitBlock();
        dbBlock_1.setStartBlock(1000L);
        dbBlock_1.setEndBlock(1500L);
        dbUnitBlocks.add(dbBlock_1);
        Mockito.when(unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(1100L,1325L)).thenReturn(dbUnitBlocks);
        Mockito.when(unitBlockRepository.saveAndFlush(Mockito.any(UnitBlock.class))).thenAnswer(i -> i.getArguments()[0]);
        
        assertEquals(2L, splitUnitBlocksService.splitUnitBlocks(proposal));
        assertEquals(1100L, dbBlock_1.getStartBlock());
        assertEquals(1325L, dbBlock_1.getEndBlock());
    }
}
