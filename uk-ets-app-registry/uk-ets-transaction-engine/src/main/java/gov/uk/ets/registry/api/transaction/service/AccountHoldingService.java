package gov.uk.ets.registry.api.transaction.service;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import gov.uk.ets.registry.api.transaction.domain.AccountBalance;
import gov.uk.ets.registry.api.transaction.domain.QUnitBlock;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

/**
 * Service for maintaining the account holdings.
 */
@Service
@AllArgsConstructor
public class AccountHoldingService {

    /**
     * The persistence context.
     */
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Repository for account holdings.
     */
    private final AccountHoldingRepository accountHoldingRepository;

    /**
     * Updates the balances of the accounts involved in the transaction.
     *
     * @param transaction The transaction.
     */
    @Transactional
    public UpdateAccountBalanceResult updateAccountBalances(Transaction transaction) {
        Long transferringIdentifier = accountHoldingRepository.getAccountIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier());
        AccountBalance transferringAccountBalance = null;
        if (transferringIdentifier != null) {
            transferringAccountBalance = updateBalanceForAccount(transferringIdentifier);
        }
        Long acquiringIdentifier = accountHoldingRepository.getAccountIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier());
        AccountBalance acquiringAccountBalance = null;
        if (acquiringIdentifier != null) {
            acquiringAccountBalance = updateBalanceForAccount(acquiringIdentifier);
        }
        
        return UpdateAccountBalanceResult
            .builder()
            .transactionIdentifier(transaction.getIdentifier())
            .lastUpdated(transaction.getLastUpdated())
            .transferringAccountBalance(transferringAccountBalance)
            .acquiringAccountBalance(acquiringAccountBalance)
            .build();
    }

    /**
     * Updates the balance of the account.
     *
     * @param accountIdentifier The account identifier.
     */
    private AccountBalance updateBalanceForAccount(Long accountIdentifier) {
        UnitType unitType = null;
        List<UnitType> unitTypes = accountHoldingRepository.getAccountUnitTypes(accountIdentifier);
        if (!CollectionUtils.isEmpty(unitTypes)) {
            unitType = UnitType.MULTIPLE;
            if (unitTypes.size() == 1) {
                unitType = unitTypes.get(0);
            }
        }
        Long balance = accountHoldingRepository.getAccountBalance(accountIdentifier);
        accountHoldingRepository.updateAccountBalance(accountIdentifier, balance, unitType);
        
        return new AccountBalance(accountIdentifier,balance, unitType);
    }
    
    
    /**
     * Returns the account holdings.
     *
     * @param accountIdentifier The account identifier.
     * @return some blocks.
     */
    public List<TransactionBlockSummary> getHoldingsOverview(Long accountIdentifier) {
        return accountHoldingRepository.getHoldingsOverview(accountIdentifier);
    }

    /**
     * Returns the account holdings.
     *
     * @param accountIdentifier The account identifier.
     * @return some blocks.
     */
    public List<TransactionBlockSummary> getHoldingsToBeReplacedForTransactionReplacement(Long accountIdentifier, String itlProjectNumber) {
        return accountHoldingRepository.getHoldingsToBeReplacedForTransactionReplacement(accountIdentifier,itlProjectNumber);
    }
    
    /**
     * Calculates the quantity held by the account.
     *
     * @param accountIdentifier The account identifier.
     * @param blockSummary      The search criteria.
     * @return a number.
     */
    public Long getQuantity(Long accountIdentifier, TransactionBlockSummary blockSummary) {
        QUnitBlock block = QUnitBlock.unitBlock;

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(block.accountIdentifier.eq(accountIdentifier));
        predicates.add(block.reservedForTransaction.isNull());

        List<Expression> groupByClauses = new ArrayList<>();
        if (blockSummary.getType() != null) {
            predicates.add(block.type.eq(blockSummary.getType()));
            groupByClauses.add(block.type);
        }

        if (blockSummary.getOriginalPeriod() != null) {
            predicates.add(block.originalPeriod.eq(blockSummary.getOriginalPeriod()));
            groupByClauses.add(block.originalPeriod);
        }

        if (blockSummary.getApplicablePeriod() != null) {
            predicates.add(block.applicablePeriod.eq(blockSummary.getApplicablePeriod()));
            groupByClauses.add(block.applicablePeriod);
        }

        if (blockSummary.getSubjectToSop() != null) {
            predicates.add(block.subjectToSop.coalesce(false).eq(blockSummary.getSubjectToSop()));
            groupByClauses.add(block.subjectToSop.coalesce(false));
        }

        if (blockSummary.getEnvironmentalActivity() != null) {
            predicates.add(block.environmentalActivity.eq(blockSummary.getEnvironmentalActivity()));
            groupByClauses.add(block.environmentalActivity);
        }

        if (StringUtils.hasText(blockSummary.getProjectNumber()) && blockSummary.getType().isRelatedWithProject()) {
            predicates.add(block.projectNumber.eq(blockSummary.getProjectNumber()));
            groupByClauses.add(block.projectNumber);
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        Long quantity = queryFactory.select(block.endBlock.subtract(block.startBlock).add(1).sum())
                .from(block)
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(groupByClauses.toArray(new Expression[0]))
                .fetchOne();

        return quantity == null ? 0L : quantity;
    }

    /**
     * Get the current account(s) balances by querying the Account Holdings Table.
     * @param transactionIdentifier
     * @param lastUpdated
     * @param transferringAccountFullIdentifier
     * @param acquiringAccountFullIdentifier
     * @return
     */
    public UpdateAccountBalanceResult getCurrentAccountBalances(String transactionIdentifier,Date lastUpdated,String transferringAccountFullIdentifier,String acquiringAccountFullIdentifier) {
        Long transferringIdentifier = accountHoldingRepository.getAccountIdentifier(transferringAccountFullIdentifier);
        Long acquiringIdentifier = accountHoldingRepository.getAccountIdentifier(acquiringAccountFullIdentifier);
        return getCurrentAccountBalances(transactionIdentifier,lastUpdated,transferringIdentifier,acquiringIdentifier);
    }
    
    /**
     * Get the current account(s) balances by querying the Account Holdings Table.
     * @param transactionIdentifier
     * @param lastUpdated
     * @param transferringAccountIdentifier
     * @param acquiringAccountFullIdentifier
     * @return
     */
    public UpdateAccountBalanceResult getCurrentAccountBalances(String transactionIdentifier,Date lastUpdated,Long transferringAccountIdentifier,String acquiringAccountFullIdentifier) {

        Long acquiringIdentifier = accountHoldingRepository.getAccountIdentifier(acquiringAccountFullIdentifier);
        return getCurrentAccountBalances(transactionIdentifier,lastUpdated,transferringAccountIdentifier,acquiringIdentifier);
    }
    
    /**
     * Get the current account(s) balances by querying the Account Holdings Table.
     * @param transactionIdentifier
     * @param lastUpdated
     * @param transferringAccountIdentifier
     * @param acquiringAccountIdentifier
     * @return
     */
    public UpdateAccountBalanceResult getCurrentAccountBalances(String transactionIdentifier,Date lastUpdated,Long transferringAccountIdentifier,Long acquiringAccountIdentifier) {

        AccountBalance transferringAccountBalance = null;
        if (transferringAccountIdentifier != null) {
            transferringAccountBalance = getCurrentBalanceForAccount(transferringAccountIdentifier);
        }
        
        AccountBalance acquiringAccountBalance = null;
        if (acquiringAccountIdentifier != null) {
            acquiringAccountBalance = getCurrentBalanceForAccount(acquiringAccountIdentifier);
        }
        
        return UpdateAccountBalanceResult
            .builder()
            .transactionIdentifier(transactionIdentifier)
            .lastUpdated(Optional.ofNullable(lastUpdated).orElse(new Date()))
            .transferringAccountBalance(transferringAccountBalance)
            .acquiringAccountBalance(acquiringAccountBalance)
            .build();
    }

    /**
     * Updates the balance of the account.
     *
     * @param accountIdentifier The account identifier.
     */
    private AccountBalance getCurrentBalanceForAccount(Long accountIdentifier) {
        UnitType unitType = null;
        List<UnitType> unitTypes = accountHoldingRepository.getAccountUnitTypes(accountIdentifier);
        if (!CollectionUtils.isEmpty(unitTypes)) {
            unitType = UnitType.MULTIPLE;
            if (unitTypes.size() == 1) {
                unitType = unitTypes.get(0);
            }
        }
        Long balance = accountHoldingRepository.getAccountBalance(accountIdentifier);
        
        return new AccountBalance(accountIdentifier,balance, unitType);
    }
}
