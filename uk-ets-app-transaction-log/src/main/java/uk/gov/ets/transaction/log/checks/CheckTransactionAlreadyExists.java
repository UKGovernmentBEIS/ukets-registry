package uk.gov.ets.transaction.log.checks;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.TransactionRepository;

/**
 * Business check: The transaction already exists in the Transaction Log.
 */
@AllArgsConstructor
@Service("check10006")
public class CheckTransactionAlreadyExists extends ParentBusinessCheck {

    /**
     * Repository for transactions.
     */
    private TransactionRepository transactionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionNotification transaction = context.getTransaction();
        Optional<Transaction> optional = transactionRepository.findByIdentifier(transaction.getIdentifier());
        if (optional.isPresent()) {
            addError(context, String.format("The transaction %s already exists in the Transaction Log", transaction.getIdentifier()));
        }
    }
}
