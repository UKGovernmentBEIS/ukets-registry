package uk.gov.ets.transaction.log.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

@Service
@AllArgsConstructor
@Log4j2
public class SplitUnitBlocksService {

    private final UnitBlockRepository unitBlockRepository;

    /**
     * Splits the unit blocks stored in the UnitBlock table as provided in the transaction proposal.
     * No unit block attributes are modified in this process.
     * 
     * @param transactionProposal the incoming registry transaction proposal
     * @return the number of splits performed
     */
    public long  splitUnitBlocks(TransactionNotification transactionProposal) {
        //Key Transaction block , Value: a list of overlapping blocks
        Map<TransactionBlock,List<UnitBlock>> overlappingBlocks = findOverlappingBlocks(transactionProposal.getBlocks());
        return splitOverlappingBlocks(overlappingBlocks);   
    }
    
    /**
     * Find the list of overlapping blocks for each TransactionBlock in the specified list.
     * @param transactionBlocks a list of the transaction unit blocks 
     * @return
     */
    private Map<TransactionBlock,List<UnitBlock>> findOverlappingBlocks(List<TransactionBlock> transactionBlocks) {
        //Key Transaction block , Value: a list of overlapping blocks
        Map<TransactionBlock,List<UnitBlock>> splitBlockCandidates = new HashMap<>();
        for (TransactionBlock proposalBlock:transactionBlocks) {
            List<UnitBlock> overlappingBlocks = unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(
                    proposalBlock.getStartBlock(), 
                    proposalBlock.getEndBlock());
            splitBlockCandidates.put(proposalBlock,overlappingBlocks);
        }
        
        return splitBlockCandidates;
    }    
    
    private long splitOverlappingBlocks(Map<TransactionBlock,List<UnitBlock>> overlappingBlocks) {
        long numOfSplits = 0;
        
        TransactionBlock[] transactionBlocks = new TransactionBlock[overlappingBlocks.size()]; 
        transactionBlocks = overlappingBlocks.keySet().toArray(transactionBlocks); 
        for (int i = 0;i < transactionBlocks.length;i++) {
            TransactionBlock transactionBlock = transactionBlocks[i];
            for (UnitBlock unitBlock : overlappingBlocks.get(transactionBlock)) {
    
                log.info("OverLapping Block was Found in Split Blocks, start: {} end: {}",unitBlock.getStartBlock(),unitBlock.getEndBlock());
                // it starts to early, split it at transactionBlock.getStartBlock
                if (unitBlock.getStartBlock() < transactionBlock.getStartBlock()) {
                    Long startTmp = unitBlock.getStartBlock();
                    log.info("Existing Block starts to early: split block from {} to {} at {}",startTmp,unitBlock.getEndBlock(),transactionBlock.getStartBlock());
                    // adjust the start
                    unitBlock.setStartBlock(transactionBlock.getStartBlock());
                    log.info("Existing Block {} with start : {} and end : {}",unitBlock.getId(),unitBlock.getStartBlock(),unitBlock.getEndBlock());
                    //save the updated block
                    unitBlock = unitBlockRepository.saveAndFlush(unitBlock);
                    // and create a new block with the part that starts to early
                    UnitBlock splittedBlock = createSplitBlock(unitBlock, startTmp,transactionBlock.getStartBlock() - 1);
                    /* new entry */
                    updateExistingListOfOverlappingTxBlocks(transactionBlocks, unitBlock,splittedBlock, i, overlappingBlocks);
                    log.info("New BLock ID {} with start : {} and end : {}",splittedBlock.getId(), splittedBlock.getStartBlock(), splittedBlock.getEndBlock());
    
                    numOfSplits++; // a split was made
                }
                if (unitBlock.getEndBlock() > transactionBlock.getEndBlock()) {
                    Long endTmp = unitBlock.getEndBlock();
                    // it is too long, split it at  transactionBlock.getUnitSerialBlockEnd
                    log.info("Blocks is to long: split block from {} to {} at {}",unitBlock.getStartBlock(),endTmp,transactionBlock.getEndBlock());
                    // adjust the end
                    unitBlock.setEndBlock(transactionBlock.getEndBlock());
                    log.info("Existing Block {} with start : {} and end : {}",unitBlock.getId(),unitBlock.getStartBlock(),unitBlock.getEndBlock());
                    //save the updated block
                    unitBlock = unitBlockRepository.saveAndFlush(unitBlock);   
                    // and create a new block for the part after the end
                    UnitBlock splittedBlock = createSplitBlock(unitBlock,transactionBlock.getEndBlock() + 1,endTmp);
                    /* new entry */
                    updateExistingListOfOverlappingTxBlocks(transactionBlocks, unitBlock,splittedBlock, i, overlappingBlocks);
                    log.info("New BLock ID {} with start : {} and end : {}" , splittedBlock.getId(),splittedBlock.getStartBlock(),splittedBlock.getEndBlock());
    
                    numOfSplits++; // a split was made
                }
            }
        }

        return numOfSplits;
    }

    /**
     * Save a newly created unit block with the specified start and end by copying the 
     * rest of the attributes from the provided splittedUnitBlock.
     * 
     * @param splittedUnitBlock the Unit Block to copy attributes from.
     * @param start Start Block of the newly created unit block
     * @param end End Block of the newly created unit block
     * @return the newly created unit block
     */
    private UnitBlock createSplitBlock(UnitBlock splittedUnitBlock, Long start, Long end) {
        UnitBlock ub = new UnitBlock();
        ub.setStartBlock(start);
        ub.setEndBlock(end);
        ub.setLastModifiedDate(new Date());
        ub.setAccountIdentifier(splittedUnitBlock.getAccountIdentifier());
        ub.setAcquisitionDate(splittedUnitBlock.getAcquisitionDate());
        ub.setType(splittedUnitBlock.getType());
        ub.setYear(splittedUnitBlock.getYear());

        return unitBlockRepository.saveAndFlush(ub);
    }

    private void updateExistingListOfOverlappingTxBlocks(TransactionBlock[] transactionBlocks, UnitBlock oldUB, UnitBlock newUB,Integer txBlockIndex, Map<TransactionBlock,List<UnitBlock>> overlappingBlocks) {

        for (int i = txBlockIndex + 1; i < transactionBlocks.length; i++) {

            TransactionBlock transactionBlock = transactionBlocks[i];

            LinkedList<UnitBlock> updatedList  = new LinkedList<>(overlappingBlocks.get(transactionBlock));

            for (UnitBlock unitBlock: updatedList) {

                if (unitBlock.getId().equals(oldUB.getId())) {

                    if (!oldUB.isOverlapping(transactionBlock)) {
                        updatedList.remove();
                    }

                    // add new unit block immediately after that
                    if (newUB.isOverlapping(transactionBlock)) {
                        updatedList.add(newUB);
                    }

                    break; // end this iteration on unit blocks
                }
            }
            overlappingBlocks.put(transactionBlock, updatedList);
        }
    } 
}
