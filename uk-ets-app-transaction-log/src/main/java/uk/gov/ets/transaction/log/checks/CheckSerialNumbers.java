package uk.gov.ets.transaction.log.checks;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

/**
 * Business check: The transferring account in the Transaction Log does not hold the serial blocks being transferred.
 */
@AllArgsConstructor
@Service("check10005")
public class CheckSerialNumbers extends ParentBusinessCheck {

    /**
     * Repository for unit blocks.
     */
    private UnitBlockRepository unitBlockRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionNotification transaction = context.getTransaction();

        if (TransactionType.IssueAllowances.equals(transaction.getType())) {
            return;
        }

        transaction.getBlocks().forEach(block -> {
            Optional<UnitBlock> holding = unitBlockRepository
                .findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
                    block.getStartBlock(), block.getEndBlock(), transaction.getTransferringAccountIdentifier()
                );
            if (holding.isEmpty()) {
                addError(context, "The transferring account in the Transaction Log does not hold the serial " +
                    "blocks being transferred");
            }
        });
    }

}
