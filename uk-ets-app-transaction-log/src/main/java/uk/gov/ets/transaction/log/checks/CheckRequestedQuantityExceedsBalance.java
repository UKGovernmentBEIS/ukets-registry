package uk.gov.ets.transaction.log.checks;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

/**
 * Business check: The requested quantity exceeds the current account balance of the transferring account in
 * the Transaction Log for the unit type being transferred.
 */
@AllArgsConstructor
@Service("check10003")
public class CheckRequestedQuantityExceedsBalance extends ParentBusinessCheck {

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

        Long availableQuantity = unitBlockRepository.calculateAvailableQuantity(
            transaction.getTransferringAccountIdentifier(),
            transaction.getUnitType());
        if (ObjectUtils.firstNonNull(availableQuantity, 0L) < transaction.getQuantity()) {
            addError(context, "The requested quantity exceeds the current account balance of the " +
                "transferring account in the Transaction Log for the unit type being transferred");
        }
    }

}
