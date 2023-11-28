package gov.uk.ets.registry.api.transaction.service;

import static java.lang.String.format;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for generating new unit blocks.
 */
@Service
@AllArgsConstructor
public class UnitCreationService {

    private final UnitBlockRepository unitBlockRepository;

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Service for marking units.
     */
    private final UnitMarkingService unitMarkingService;

    /**
     * Generates a new transaction block (e.g. during Issuance).
     *
     * @param transaction            The transaction.
     * @param firstBlock             The block details.
     * @param originatingCountryCode The originating country code.
     */
    @Transactional
    public void generateTransactionBlock(TransactionSummary transaction, TransactionBlockSummary firstBlock, String originatingCountryCode) {
        Long startBlock = unitBlockRepository.getNextAvailableSerialNumber();
        Long endBlock = startBlock + transaction.calculateQuantity() - 1;

        TransactionBlock newBlock = new TransactionBlock();
        newBlock.setStartBlock(startBlock);
        newBlock.setEndBlock(endBlock);
        newBlock.setApplicablePeriod(firstBlock.getApplicablePeriod());
        newBlock.setOriginalPeriod(firstBlock.getOriginalPeriod());
        newBlock.setEnvironmentalActivity(firstBlock.getEnvironmentalActivity());
        newBlock.setSubjectToSop(unitMarkingService.markUnitsAsSubjectToSop(transaction.getType(), firstBlock));
        newBlock.setYear(firstBlock.getYear());

        newBlock.setOriginatingCountryCode(originatingCountryCode);
        newBlock.setType(firstBlock.getType());
        newBlock.setTransaction(transactionPersistenceService.getTransaction(transaction.getIdentifier()));
        transactionPersistenceService.save(newBlock);

        unitBlockRepository.updateNextAvailableSerialNumber(endBlock + 1);
    }

    /**
     * Generates new transaction blocks (e.g. during inbound transactions).
     *
     * @param transactionSummary The transaction.
     */
    @Transactional
    public void generateTransactionBlocks(TransactionSummary transactionSummary) {
        Transaction transaction = transactionPersistenceService.getTransaction(transactionSummary.getIdentifier());
        for (TransactionBlockSummary block : transactionSummary.getBlocks()) {
            TransactionBlock newBlock = new TransactionBlock();
            newBlock.setType(block.getType());
            newBlock.setStartBlock(block.getStartBlock());
            newBlock.setEndBlock(block.getEndBlock());
            newBlock.setOriginatingCountryCode(block.getOriginatingCountryCode());
            newBlock.setOriginalPeriod(block.getOriginalPeriod());
            newBlock.setApplicablePeriod(block.getApplicablePeriod());
            newBlock.setSubjectToSop(block.getSubjectToSop());
            newBlock.setEnvironmentalActivity(block.getEnvironmentalActivity());
            newBlock.setProjectNumber(block.getProjectNumber());
            newBlock.setProjectTrack(block.getProjectTrack());
            newBlock.setExpiryDate(block.getExpiryDate());
            newBlock.setTransaction(transaction);
            transactionPersistenceService.save(newBlock);
        }
    }

    /**
     * Creates new unit blocks based on the provided transaction blocks.
     *
     * @param acquiringAccountIdentifier The acquiring account identifier.
     * @param blocks                     The transaction blocks.
     */
    @Transactional
    public void createUnitBlocks(Long acquiringAccountIdentifier, List<TransactionBlock> blocks) {
        if (CollectionUtils.isEmpty(blocks)) {
            return;
        }
        try {
            Date acquisitionDate = new Date();
            for (TransactionBlock block : blocks) {
                UnitBlock newBlock = new UnitBlock();
                BeanUtils.copyProperties(newBlock, block);
                newBlock.setId(null);
                newBlock.setAccountIdentifier(acquiringAccountIdentifier);
                newBlock.setAcquisitionDate(acquisitionDate);
                transactionPersistenceService.save(newBlock);
            }
        } catch (IllegalAccessException | InvocationTargetException exc) {
            throw new TransactionExecutionException(this.getClass(), format("Error when generating creating unit blocks for account %d", acquiringAccountIdentifier), exc);
        }
    }

    /**
     * Creates new transaction blocks based on the provided unit blocks.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Transactional
    public void createTransactionBlocks(String transactionIdentifier) {
        createTransactionBlocks(transactionIdentifier, null, null);
    }

    @Transactional
    public void createTransactionBlocksForReplacement(String transactionIdentifier) {
        List<UnitBlock> blocks = transactionPersistenceService.getUnitBlocksForReplacement(transactionIdentifier);
        createTransactionBlocks(blocks, transactionIdentifier, null, null);
    }

    /**
     * Creates new transaction blocks based on the provided unit blocks.
     *
     * @param transactionIdentifier The transaction identifier.
     * @param projectNumber         The project number
     * @param projectTrack          The project track
     */
    @Transactional
    public void createTransactionBlocks(String transactionIdentifier, String projectNumber, ProjectTrack projectTrack) {
        List<UnitBlock> unitBlocks = transactionPersistenceService.getUnitBlocks(transactionIdentifier);
        createTransactionBlocks(unitBlocks, transactionIdentifier, projectNumber, projectTrack);
    }

    /**
     * Creates new transaction blocks based on the provided unit blocks.
     *
     * @param unitBlocks            The unit blocks.
     * @param transactionIdentifier The transaction identifier.
     * @param projectNumber         The project number
     * @param projectTrack          The project track
     */
    @Transactional
    public void createTransactionBlocks(List<UnitBlock> unitBlocks, String transactionIdentifier, String projectNumber, ProjectTrack projectTrack) {
        Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
        if (CollectionUtils.isEmpty(unitBlocks)) {
            return;
        }
        try {
            for (UnitBlock block : unitBlocks) {
                TransactionBlock newBlock = new TransactionBlock();
                BeanUtils.copyProperties(newBlock, block);
                newBlock.setId(null);
                if (projectNumber != null) {
                    newBlock.setProjectNumber(projectNumber);
                }
                if (projectTrack != null) {
                    newBlock.setProjectTrack(projectTrack);
                }
                newBlock.setTransaction(transaction);
                newBlock.setBlockRole(block.getReservedForReplacement() != null ? "REP" : null);
                transactionPersistenceService.save(newBlock);
            }
        } catch (IllegalAccessException | InvocationTargetException exc) {
            throw new TransactionExecutionException(this.getClass(), "Error when generating transaction blocks", exc);
        }
    }

}
