package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.Block;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for marking and clearing units.
 */
@Service
@AllArgsConstructor
public class UnitMarkingService {

    /**
     * Persistence service.
     */
    private TransactionPersistenceService transactionPersistenceService;

    /**
     * Determines whether this block should be marked as "Subject to SOP".
     *
     * @param type  The transaction type.
     * @param block The block.
     * @return false/true
     */
    public Boolean markUnitsAsSubjectToSop(TransactionType type, TransactionBlockSummary block) {
        return TransactionType.IssueOfAAUsAndRMUs.equals(type) &&
                UnitType.AAU.equals(block.getType()) &&
                CommitmentPeriod.CP2.equals(block.getOriginalPeriod()) &&
                CommitmentPeriod.CP2.equals(block.getApplicablePeriod());
    }

    /**
     * Marks the applicable period of the provided unit blocks.
     *
     * @param blocks           The unit blocks.
     * @param commitmentPeriod The commitment period.
     */
    @Transactional
    public <T extends Block> List<T> markApplicablePeriod(List<T> blocks, CommitmentPeriod commitmentPeriod) {
        Date now = new Date();
        for (Block block : blocks) {
            block.setApplicablePeriod(commitmentPeriod);
            if (block instanceof UnitBlock) {
                ((UnitBlock) block).setLastModifiedDate(now);
            }
            transactionPersistenceService.save(block);
        }
        return blocks;
    }

    /**
     * Marks the project number, project track and unit type of the provided unit blocks.
     *
     * @param blocks            The unit blocks.
     * @param transactionBlocks The transaction blocks
     */
    @Transactional
    public void markProjectAndConvertToERU(List<UnitBlock> blocks, List<TransactionBlock> transactionBlocks,
                                           boolean convertedForSOP) {
        TransactionBlock transactionBlock = transactionBlocks.get(0);
        Date now = new Date();
        for (UnitBlock block : blocks) {
            block.setProjectNumber(transactionBlock.getProjectNumber());
            block.setProjectTrack(transactionBlock.getProjectTrack());
            block.setType(transactionBlock.getType());
            block.setSubjectToSop(convertedForSOP);
            block.setLastModifiedDate(now);
            transactionPersistenceService.save(block);
        }
    }

    /**
     * Marks the type and subject to SOP flag of the provided unit blocks
     *
     * @param blocks            The unit blocks
     * @param convertedForSOP   The flag for is converted for SOP
     */
    @Transactional
    public void markToERU(List<TransactionBlock> blocks, boolean convertedForSOP) {
        for (TransactionBlock block : blocks) {
            block.setType(block.getType().equals(UnitType.AAU) ? UnitType.ERU_FROM_AAU : UnitType.ERU_FROM_RMU);
            block.setSubjectToSop(convertedForSOP);
            transactionPersistenceService.save(block);
        }
    }


    /**
     * Change the expiration date of the provided unit blocks
     *
     * @param blocks     The unit blocks.
     * @param expireDate The new expiration date.
     */
    @Transactional
    public <T extends Block> void changeExpireDate(List<T> blocks, Date expireDate) {
        for (Block block : blocks) {
            block.setExpiryDate(expireDate);
            transactionPersistenceService.save(block);
        }
    }

    /**
     * Marks the related fields of the Replacement Transaction type.
     *
     * @param blocks The unit blocks to mark
     */
    @Transactional
    public void markUnitsForReplacement(List<UnitBlock> blocks) {
        for (UnitBlock block : blocks) {
            block.setReplaced(true);
            block.setReservedForReplacement(null);
        }
    }
}
