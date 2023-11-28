package uk.gov.ets.transaction.log.checks;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;
import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.AccountRepository;

/**
 * Business check: The transferring account number does not exist in the Transaction Log.
 */
@AllArgsConstructor
@Service("check10001")
public class CheckTransferringAccountExists extends ParentBusinessCheck {

    /**
     * Repository for accounts.
     */
    private AccountRepository accountRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionNotification transaction = context.getTransaction();
        Optional<Account> account = accountRepository.findByIdentifier(transaction.getTransferringAccountIdentifier());
        if (account.isEmpty()) {
            addError(context, "The transferring account number does not exist in the Transaction Log");
        }
    }

}
