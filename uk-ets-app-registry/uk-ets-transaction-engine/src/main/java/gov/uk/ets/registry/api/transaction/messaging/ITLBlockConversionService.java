package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.ProjectService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import uk.gov.ets.lib.commons.kyoto.types.TransactionUnitBlock;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for preparing messages for ITL communication, focusing on transaction blocks.
 */
@Service
@AllArgsConstructor
public class ITLBlockConversionService {

    /**
     * Service for projects.
     */
    private ProjectService projectService;

    /**
     * Converts the transaction blocks.
     * @param transaction The transaction.
     * @param blocks The transaction blocks.
     * @return some transaction unit blocks.
     */
    public TransactionUnitBlock[] convert(Transaction transaction, List<TransactionBlock> blocks) {
        if (CollectionUtils.isEmpty(blocks)) {
            return null;
        }

        List<TransactionUnitBlock> result = new ArrayList<>();

        for (TransactionBlock transactionBlock : blocks) {
            TransactionUnitBlock transactionUnitBlock = new TransactionUnitBlock();

            UnitType unitType = transactionBlock.getType();
            transactionUnitBlock.setUnitSerialBlockStart(transactionBlock.getStartBlock());
            transactionUnitBlock.setUnitSerialBlockEnd(transactionBlock.getEndBlock());
            transactionUnitBlock.setUnitType(unitType.getPrimaryCode());
            transactionUnitBlock.setSuppUnitType(unitType.getSupplementaryCode());
            transactionUnitBlock.setOriginatingRegistryCode(transactionBlock.getOriginatingCountryCode());
            transactionUnitBlock.setOriginalCommitPeriod(transactionBlock.getOriginalPeriod().getCode());
            transactionUnitBlock.setApplicableCommitPeriod(transactionBlock.getApplicablePeriod().getCode());
            transactionUnitBlock.setExpiryDate(transactionBlock.getExpiryDate());

            transactionUnitBlock.setTransferringRegistryAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier());
            transactionUnitBlock.setTransferringRegistryAccountType(transaction.getTransferringAccount().getAccountType().getCode());

            transactionUnitBlock.setAcquiringRegistryAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier());
            transactionUnitBlock.setAcquiringRegistryAccountType(transaction.getAcquiringAccount().getAccountType().getCode());

            if (transactionBlock.getEnvironmentalActivity() != null) {
                transactionUnitBlock.setLULUCFActivity(transactionBlock.getEnvironmentalActivity().getCode());
            }

            if (transactionBlock.getProjectNumber() != null) {
                transactionUnitBlock.setProjectIdentifier(projectService.extractProjectIdentifier(transactionBlock.getProjectNumber()));
            }
            if (transactionBlock.getProjectTrack() != null) {
                transactionUnitBlock.setTrack(transactionBlock.getProjectTrack().getCode());
            }
            if(transactionBlock.getBlockRole() != null) {
                transactionUnitBlock.setBlockRole(!transactionBlock.getBlockRole().isEmpty() ? transactionBlock.getBlockRole() : null);
            }

            result.add(transactionUnitBlock);
        }
        return result.toArray(new TransactionUnitBlock[0]);
    }

    /**
     * Converts the transaction unit blocks.
     * @param blocks The transaction unit blocks.
     * @return some transaction blocks.
     */
    public List<TransactionBlockSummary> convert(TransactionUnitBlock[] blocks) {
        if (ArrayUtils.isEmpty(blocks)) {
            return null;
        }

        List<TransactionBlockSummary> result = new ArrayList<>();
        for (TransactionUnitBlock block : blocks) {
            TransactionBlockSummary transactionBlock = new TransactionBlockSummary();

            transactionBlock.setStartBlock(block.getUnitSerialBlockStart());
            transactionBlock.setEndBlock(block.getUnitSerialBlockEnd());
            if (transactionBlock.getStartBlock() != null && transactionBlock.getEndBlock() != null) {
                Long quantity = transactionBlock.getEndBlock() - transactionBlock.getStartBlock() + 1;
                transactionBlock.setQuantity(String.valueOf(quantity));
            }
            transactionBlock.setType(UnitType.of(block.getUnitType(), Optional.ofNullable(block.getSuppUnitType()).orElse(0)));
            transactionBlock.setOriginatingCountryCode(block.getOriginatingRegistryCode());
            transactionBlock.setOriginalPeriod(CommitmentPeriod.findByCode(block.getOriginalCommitPeriod()));
            transactionBlock.setApplicablePeriod(CommitmentPeriod.findByCode(block.getApplicableCommitPeriod()));
            transactionBlock.setExpiryDate(block.getExpiryDate());

            if (block.getLULUCFActivity() != null) {
                transactionBlock.setEnvironmentalActivity(EnvironmentalActivity.of(block.getLULUCFActivity()));
            }

            if (block.getProjectIdentifier() != null) {
                transactionBlock.setProjectNumber(String.format("%s%s", block.getOriginatingRegistryCode(), block.getProjectIdentifier()));
            }
            
            if(!Optional.ofNullable(block.getTrack()).isEmpty()) {
                transactionBlock.setProjectTrack(ProjectTrack.of(block.getTrack().intValue()));	
            }

            result.add(transactionBlock);
        }
        return result;
    }

}
