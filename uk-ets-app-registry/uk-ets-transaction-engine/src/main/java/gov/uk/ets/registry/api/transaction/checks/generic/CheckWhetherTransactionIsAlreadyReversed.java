package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This transaction has already been reversed.
 */
@Service("check3019")
@RequiredArgsConstructor
public class CheckWhetherTransactionIsAlreadyReversed extends ParentBusinessCheck {

    private final TransactionConnectionRepository transactionConnectionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        Transaction transaction = transactionRepository.findByIdentifier(context.getTransaction()
            .getReversedIdentifier());

        List<TransactionStatus> statuses = TransactionStatus.getPendingStatuses();
        statuses.add(TransactionStatus.COMPLETED);

        Long count = transactionConnectionRepository.countByObjectTransactionAndTypeAndSubjectTransaction_StatusIn(
            transaction, TransactionConnectionType.REVERSES, statuses
        );

        if (count > 0) {
            addError(context, "This transaction has already been reversed");
        }

    }

}
