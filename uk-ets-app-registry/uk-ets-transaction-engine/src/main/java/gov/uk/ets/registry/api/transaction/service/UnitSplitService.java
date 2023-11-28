package gov.uk.ets.registry.api.transaction.service;

import static java.lang.String.format;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for splitting unit blocks.
 */
@Service
@AllArgsConstructor
public class UnitSplitService {

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Splits the provided unit block.
     * @param block The unit block to split.
     * @param splitFromLeft Whether to split from left.
     * @param newUpperLimit The new upper limit.
     * @return the new unit block.
     */
    @Transactional
    public UnitBlock split(UnitBlock block, boolean splitFromLeft, long newUpperLimit) {

        UnitBlock result = new UnitBlock();
        try {
            BeanUtils.copyProperties(result, block);
            result.setId(null);

            Long currentStart = block.getStartBlock();
            Long currentEnd = block.getEndBlock();

            if (splitFromLeft) {

                if (newUpperLimit == currentStart) {
                    // No split is required
                    return null;

                } else if (newUpperLimit < currentStart) {
                    throw new IllegalArgumentException(format("Invalid values provided, since %d is lower than %d", newUpperLimit, currentStart));
                }

                block.setStartBlock(newUpperLimit);
                result.setStartBlock(currentStart);
                result.setEndBlock(newUpperLimit - 1);

            } else {

                if (newUpperLimit == currentEnd) {
                    // No split is required
                    return null;

                } else if (newUpperLimit > currentEnd) {
                    throw new IllegalArgumentException(format("Invalid values provided, since %d is larger than %d", newUpperLimit, currentEnd));
                }

                block.setEndBlock(newUpperLimit);
                result.setStartBlock(newUpperLimit + 1);
                result.setEndBlock(currentEnd);
            }
            transactionPersistenceService.saveAndFlush(block);
            result = transactionPersistenceService.save(result);

        } catch (Exception exc) {
            throw new TransactionExecutionException(getClass(), "Exception when splitting a unit block.", exc);
        }

        return result;
    }

}
