package gov.uk.ets.registry.api.reconciliation.service;

import gov.uk.ets.registry.api.transaction.domain.QTransaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Executes queries against the {@link TransactionRepository} transaction repository regarding ETS transactions.
 */
@Service
@AllArgsConstructor
public class ETSTransactionService {

    private static final QTransaction transaction = QTransaction.transaction;
    private final TransactionRepository transactionRepository;

    /**
     * Gets the total number of the pending ETS transactions.
     *
     * @return the total number of the pending ETS transactions.
     */
    public long countPendingETSTransactions() {
        List<TransactionType> nonKyotoTransactionTypes = Stream.of(TransactionType.values())
            .filter(transactionType ->
                !transactionType.isKyoto()).collect(Collectors.toList());
        return transactionRepository.count(transaction.type.in(nonKyotoTransactionTypes)
            .and(transaction.status.notIn(getAllowableTransactionStatusesForReconciliationInitiation())));
    }
    
    private List<TransactionStatus> getAllowableTransactionStatusesForReconciliationInitiation() {
    	return Stream.of(List.of(TransactionStatus.AWAITING_APPROVAL, TransactionStatus.DELAYED), TransactionStatus.getFinalStatuses())
    		.flatMap(Collection::stream).collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));

    }
}
