package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This transaction could be reversed up to 14 calendar days from the original request.
 */
@Service("check3020")
public class CheckValidReverseSurrenderPeriod extends ParentBusinessCheck {

    @Value("${business.property.transaction.reverse.transaction.active.days:14}")
    private Integer days;

    @Override
    public void execute(BusinessCheckContext context) {

        Transaction transaction = transactionRepository.findByIdentifier(context.getTransaction()
            .getReversedIdentifier());

        LocalDateTime transactionCompletion = transaction.getLastUpdated()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        if (LocalDateTime.now().isAfter(transactionCompletion.plusDays(days))) {
            addError(context,
                String.format("This transaction could be reversed up to %d calendar day%s from the original request",
                    days, days > 1 ? "s" : ""));
        }

    }
}
