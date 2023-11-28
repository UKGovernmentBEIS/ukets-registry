package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for transferring units.
 */
@Service
@AllArgsConstructor
public class UnitTransferService {

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Repository for unit blocks.
     */
    private final UnitBlockRepository unitBlockRepository;

    /**
     * Transfers the units of this transaction outside the registry.
     * @param transactionIdentifier The transaction identifier.
     */
    @Transactional
    public void transferUnitsOutsideRegistry(String transactionIdentifier) {
        List<UnitBlock> blocks = transactionPersistenceService.getUnitBlocks(transactionIdentifier);
        for (UnitBlock block : blocks) {
            transactionPersistenceService.delete(block);
        }
    }

    /**
     * Transfers the units of this transaction inside the registry.
     * @param transactionIdentifier The transaction identifier.
     * @param acquiringAccountIdentifier The acquiring account identifier.
     */
    @Transactional
    public void transferUnitsInsideRegistry(String transactionIdentifier, Long acquiringAccountIdentifier) {
        unitBlockRepository.acquireReservedBlocks(transactionIdentifier, acquiringAccountIdentifier, new Date());
    }

}
