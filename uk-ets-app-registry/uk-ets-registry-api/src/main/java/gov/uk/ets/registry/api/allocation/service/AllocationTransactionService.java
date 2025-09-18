package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@AllArgsConstructor
public class AllocationTransactionService {

    /**
     * Service for transactions.
     */
    private TransactionService transactionService;

    /**
     * Executes an allocation transaction.
     * @param transferringAccountIdentifier The transferring account identifier.
     * @param acquiringAccountFullIdentifier The acquiring account identifier.
     * @param quantity The quantity to allocate.
     * @param allocationYear The allocation year.
     * @param allocationType The allocation type.
     * @return Transaction identifier.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BusinessCheckResult executeAllocation(Long transferringAccountIdentifier,
                                                 String acquiringAccountFullIdentifier,
                                                 Long quantity,
                                                 Integer allocationYear,
                                                 AllocationType allocationType) {

        log.info("Proposing allocation from={}, to={}, quantity={}, year={}, plan={}",
            transferringAccountIdentifier, acquiringAccountFullIdentifier, quantity, allocationYear, allocationType);

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.AllocateAllowances);
        transaction.setTransferringAccountIdentifier(transferringAccountIdentifier);
        transaction.setAcquiringAccountFullIdentifier(acquiringAccountFullIdentifier);
        Map<String, Serializable> map = new HashMap<>();
        map.put(AllocationYear.class.getSimpleName(), allocationYear);
        map.put(AllocationType.class.getSimpleName(), allocationType);
        transaction.setAdditionalAttributes(map);

        TransactionBlockSummary block = new TransactionBlockSummary();
        block.setType(UnitType.ALLOWANCE);
        block.setQuantity(String.valueOf(quantity));
        transaction.setBlocks(Arrays.asList(block));

        BusinessCheckResult result = new BusinessCheckResult();
        try {
            result = transactionService.proposeTransaction(transaction, true);

        } catch (BusinessCheckException exc) {
            result.setErrors(exc.getErrors());
        }

        log.info("Proposed allocation from={}, to={}, quantity={}, year={}, plan={}, success={}, " +
                "identifier={}, errors={}",
            transferringAccountIdentifier, acquiringAccountFullIdentifier, quantity, allocationYear, allocationType,
            result.success(), result.getTransactionIdentifier(), result.getErrors());

        return result;
    }

}
