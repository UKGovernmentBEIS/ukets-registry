package uk.gov.ets.transaction.log.checks;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.service.LimitService;

/**
 * Business check: The quantity of allowances issued must not exceed the issuance limit set in the Transaction Log.
 */
@AllArgsConstructor
@Service("check10004")
public class CheckIssuanceLimits extends ParentBusinessCheck {

    /**
     * Repository for allocation phases.
     */
    private LimitService limitService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionNotification transaction = context.getTransaction();
        if (!TransactionType.IssueAllowances.equals(transaction.getType())) {
            return;
        }

        if (ObjectUtils.firstNonNull(limitService.getIssuanceLimit(), 0L) -
            ObjectUtils.firstNonNull(transaction.getQuantity(), 0L) < 0) {
            addError(context, "The quantity of allowances issued must not exceed the issuance limit set in the Transaction Log");
        }
    }

}
