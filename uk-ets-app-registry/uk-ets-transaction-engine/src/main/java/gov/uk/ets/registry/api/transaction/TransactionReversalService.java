package gov.uk.ets.registry.api.transaction;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckService;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Service for reversing transactions.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionReversalService {

    /**
     * The persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * The service for business checks.
     */
    private final BusinessCheckService businessCheckService;

    private static final EnumMap<TransactionType, TransactionType> reversedTypes = new EnumMap<>(TransactionType.class);

    @PostConstruct
    public void init() {
        reversedTypes.put(TransactionType.AllocateAllowances, TransactionType.ReverseAllocateAllowances);
        reversedTypes.put(TransactionType.SurrenderAllowances, TransactionType.ReverseSurrenderAllowances);
        reversedTypes.put(TransactionType.DeletionOfAllowances, TransactionType.ReverseDeletionOfAllowances);
    }

    /**
     * Returns the reversed transaction type.
     * @param transactionType The transaction type.
     * @return the reversed transaction type.
     */
    public TransactionType getReversedByType(TransactionType transactionType) {
        return reversedTypes.get(transactionType);
    }

    /**
     * Returns whether this transaction has a reversed type.
     * @param transactionType The transaction type.
     * @return false/true
     */
    public boolean hasReversedType(TransactionType transactionType) {
        return getReversedByType(transactionType) != null;
    }

    /**
     * Makes preparatory actions for transaction reversals.
     * @param transaction The transaction summary.
     */
    public void prepareReversal(TransactionSummary transaction) {
        prepareReversal(transaction, null);
    }

    /**
     * Makes preparatory actions for transaction reversals.
     * @param transaction The transaction summary.
     */
    public void prepareReversal(TransactionSummary transaction, Transaction initialTransaction) {
        final String reversedIdentifier = transaction.getReversedIdentifier();
        if (StringUtils.isEmpty(reversedIdentifier) && initialTransaction == null) {
            return;
        }
        if (initialTransaction == null) {
            initialTransaction = transactionPersistenceService.getTransaction(reversedIdentifier);
        }
        TransactionType initialType = initialTransaction.getType();
        if (hasReversedType(initialType)) {
            transaction.setType(getReversedByType(initialType));
            transaction.setBlocks(Collections.singletonList(TransactionBlockSummary.builder()
                .quantity(initialTransaction.getQuantity().toString())
                .type(initialType.getUnits().get(0))
                .build()));
            if (!ObjectUtils.isEmpty(initialTransaction.getAttributes())) {
                transaction.setAdditionalAttributes(getAdditionalAttributes(initialTransaction.getAttributes()));
            }
            if (initialTransaction.getIdentifier().equals(transaction.getIdentifier())) {
                transaction.setIdentifier(format("%s%s", Constants.getRegistryCode(initialType.isKyoto()),
                    transactionPersistenceService.getNextIdentifier()));
            }
        }
    }

    /**
     * Retrieves the additional attributes.
     *
     * @param attributes The transaction attributes.
     * @return the additional attributes.
     */
    private Map<String, Serializable> getAdditionalAttributes(String attributes) {
        try {
            return new ObjectMapper().readValue(attributes, Map.class);

        } catch (JsonProcessingException exception) {
            throw new TransactionExecutionException(this.getClass(),
                "Error when de-serialising additional transaction attributes.", exception);
        }
    }

    /**
     * Checks whether the provided transaction is allowed to be reversed.
     * @param transaction The transaction.
     * @return false/true
     */
    public boolean canBeReversed(Transaction transaction) {
        if (!transaction.getType().getReversalAllowed() ||
            !TransactionStatus.COMPLETED.equals(transaction.getStatus())) {
            return false;
        }

        boolean businessChecks = true;
        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .reversedIdentifier(transaction.getIdentifier()).build();
        prepareReversal(transactionSummary, transaction);
        try {
            businessCheckService.performChecks(transactionSummary, true);
        } catch (BusinessCheckException businessCheckException) {
            businessCheckException.getErrors().forEach(error ->
                log.info("Transaction {} cannot be reversed due to the following business error {}",
                    transaction.getIdentifier(), error));
            businessChecks = false;
        }

        return businessChecks;
    }

}
