package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.TransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.TransactionHistory;
import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Persistence service for transactions.
 */
@Service
@RequiredArgsConstructor
public class TransactionPersistenceService {

    /**
     * Persistence service for transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Persistence service for transaction blocks.
     */
    private final TransactionBlockRepository transactionBlockRepository;

    /**
     * Persistence service for unit blocks.
     */
    private final UnitBlockRepository unitBlockRepository;

    /**
     * Persistence service for accounts.
     */
    private final AccountTotalRepository accountTotalRepository;

    private final TransactionResponseRepository transactionResponseRepository;

    private final TransactionHistoryRepository transactionHistoryRepository;

    private final TransactionConnectionRepository transactionConnectionRepository;

    /**
     * The persistence context.
     */
    private EntityManager entityManager;

    /**
     * Returns the next available transaction identifier.
     * @return a number
     */
    public Long getNextIdentifier() {
        return transactionRepository.getNextTransactionBusinessIdentifier();
    }

    /**
     * Returns the transaction with the provided identifier.
     * @param transactionIdentifier The unique transaction business identifier.
     * @return a transaction.
     */
    public Transaction getTransaction(String transactionIdentifier) {
        return transactionRepository.findByIdentifier(transactionIdentifier);
    }

    /**
     * Returns the transaction and its blocks with the provided identifier .
     * @param transactionIdentifier The unique transaction business identifier.
     * @return a transaction.
     */
    public Transaction getTransactionWithBlocks(String transactionIdentifier) {
        return transactionRepository.findTransactionWithBlocksByIdentifier(transactionIdentifier);
    }

    /**
     * Returns the blocks of the provided transaction.
     * @param transactionIdentifier The unique transaction business identifier.
     * @return some transaction blocks.
     */
    public List<TransactionBlock> getTransactionBlocks(String transactionIdentifier) {
        return transactionBlockRepository.findByTransaction_Identifier(transactionIdentifier);
    }

    /**
     * Returns the unit blocks reserved for this transaction.
     * @param transactionIdentifier The transaction identifier.
     * @return some unit blocks.
     */
    public List<UnitBlock> getUnitBlocks(String transactionIdentifier) {
        return unitBlockRepository.findByReservedForTransaction(transactionIdentifier);
    }

    /**
     * Returns the unit blocks reserved for this Replacement transaction.
     * @param transactionIdentifier The transaction identifier.
     * @return some unit blocks.
     */
    public List<UnitBlock> getUnitBlocksForReplacement(String transactionIdentifier) {
        return unitBlockRepository.findByReservedForReplacement(transactionIdentifier);
    }

    /**
     * Retrieves account details based on the business identifier.
     * @param accountIdentifier The account identifier.
     * @return the account summary.
     */
    public AccountSummary getAccount(Long accountIdentifier) {
        return accountTotalRepository.getAccountSummary(accountIdentifier);
    }

    /**
     * Retrieves account details based on the full identifier.
     * @param accountFullIdentifier The account full identifier.
     * @return the account summary.
     */
    public AccountSummary getAccount(String accountFullIdentifier) {
        return accountTotalRepository.getAccountSummary(accountFullIdentifier);
    }

    /**
     * Retrieves account details based on the business identifier.
     * @param registryAccountType The registry account type.
     * @param kyotoAccountType The kyoto account type.
     * @param accountStatus The account status.
     * @return the account summary.
     */
    public AccountSummary getAccount(RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType, List<AccountStatus> accountStatus, CommitmentPeriod commitmentPeriod) {
        List<AccountSummary> list = accountTotalRepository.getAccountSummary(registryAccountType, kyotoAccountType, accountStatus, commitmentPeriod.getCode());
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * Retrieves account details.
     * @param accountType The account type.
     * @param accountStatus The account status.
     * @return the account summary.
     */
    public AccountSummary getAccount(AccountType accountType, AccountStatus accountStatus) {
        List<AccountSummary> list = accountTotalRepository.getAccountSummary(accountType.getRegistryType(), accountType.getKyotoType(), accountStatus);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * Saves the provided entity.
     * @param entity The entity.
     * @param <E> The parameterised type.
     * @return the persisted entity.
     */
    @Transactional
    public <E> E save(E entity) {
        E result = null;

        if (entity instanceof Transaction) {
            result = (E)transactionRepository.save((Transaction)entity);

        } else if (entity instanceof TransactionBlock) {
            result = (E)transactionBlockRepository.save((TransactionBlock)entity);}

        else if (entity instanceof TransactionHistory) {
            result = (E)transactionHistoryRepository.save((TransactionHistory)entity);

        } else if (entity instanceof UnitBlock) {
            result = (E)unitBlockRepository.save((UnitBlock)entity);

        } else if (entity instanceof TransactionConnection) {
            result = (E)transactionConnectionRepository.save((TransactionConnection)entity);

        } else {
            classNotSupported(entity);
        }
        return result;
    }

   /**
     * Saves and flushes the provided entity.
     * @param entity The entity.
     * @param <E> The parameterised type.
     * @return the persisted entity.
     */
    @Transactional
    public <E> E saveAndFlush(E entity) {
        E result = null;

        if (entity instanceof Transaction) {
            result = (E)transactionRepository.saveAndFlush((Transaction)entity);

        } else if (entity instanceof TransactionBlock) {
            result = (E)transactionBlockRepository.saveAndFlush((TransactionBlock)entity);

        } else if (entity instanceof UnitBlock) {
            result = (E)unitBlockRepository.saveAndFlush((UnitBlock)entity);

        } else {
            classNotSupported(entity);
        }
        return result;
    }

    /**
     * Deletes the provided entity.
     * @param entity The entity.
     * @param <E> The parameterised type.
     */
    @Transactional
    public <E> void delete(E entity) {
        if (entity instanceof UnitBlock) {
            unitBlockRepository.delete((UnitBlock)entity);

        } else {
            classNotSupported(entity);
        }
    }

    /**
     * Performs a query.
     * @param input The query string.
     * @param parameters The query parameters.
     * @param lockMode The lock mode.
     * @param <T> The entity type.
     * @return a list of entities.
     */
    public <T> List<T> find(String input, Map<SelectionCriteria, Object> parameters, LockModeType lockMode) {
        Query query = entityManager.createQuery(input);

        for (Map.Entry<SelectionCriteria, Object> entry : parameters.entrySet()) {
            if (entry.getValue() != null) {
                query.setParameter(entry.getKey().name(), entry.getValue());
            }
        }

        if (lockMode != null) {
            query.setLockMode(lockMode);
        }
        return query.getResultList();
    }

    /**
     * Updates the status of the provided transaction.
     * @param transaction The transaction.
     * @param newTransactionStatus The transaction status.
     */
    @Transactional
    public void updateStatus(Transaction transaction, TransactionStatus newTransactionStatus) {
        final Date date = new Date();
        transaction.setLastUpdated(date);
        transaction.setStatus(newTransactionStatus);
        save(transaction);

        TransactionHistory history = new TransactionHistory();
        history.setDate(date);
        history.setStatus(newTransactionStatus);
        history.setTransaction(transaction);
        save(history);
    }

    /**
     * Saves the transaction response entities associated with a transaction
     *
     * @param dto The {@link TransactionResponseDTO} transaction errors and warnings data transfer input
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveTransactionResponse(TransactionResponseDTO dto) {
        if (CollectionUtils.isEmpty(dto.getErrors())) {
            return;
        }
        if (dto.getTransaction() == null) {
            throw new IllegalArgumentException("The transaction identifier should not be null");
        }
        Transaction transaction = dto.getTransaction();
        dto.getErrors().forEach(error -> {
            TransactionResponse response = new TransactionResponse();
            response.setDateOccurred(new Date());
            response.setDetails(error.getMessage());
            response.setErrorCode((long)error.getCode());
            response.setTransaction(transaction);

            transactionResponseRepository.save(response);
        });
    }

    /**
     * Denotes that the entity is not supported.
     * @param entity The entity.
     * @param <E> The parameter.
     */
    private <E> void classNotSupported(E entity) {
        throw new IllegalArgumentException(String.format("Entity not supported: %s", entity.getClass()));
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TransactionConnection getTransactionConnection(Transaction transaction,
                                                          TransactionConnectionType type,
                                                          boolean findBySubject) {
        TransactionConnection result = null;
        Optional<TransactionConnection> connection =
            findBySubject ?
            transactionConnectionRepository.findBySubjectTransactionAndType(transaction, type) :
            transactionConnectionRepository.findFirstByObjectTransactionAndTypeOrderByDateDesc(transaction, type);
        if (connection.isPresent()) {
            result = connection.get();
        }
        return result;
    }
}
