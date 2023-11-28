package gov.uk.ets.registry.api.transaction.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import gov.uk.ets.registry.api.transaction.domain.AccountBalance;
import gov.uk.ets.registry.api.transaction.domain.TransactionAccountBalance;

public interface TransactionAccountBalanceRepository extends JpaRepository<TransactionAccountBalance, Long>, QuerydslPredicateExecutor<TransactionAccountBalance> {

    Optional<TransactionAccountBalance> findByTransactionIdentifier(String transactionIdentifier);    
    
    @Query("select new gov.uk.ets.registry.api.transaction.domain.AccountBalance("
        + "case"
        + "   when transferringAccountIdentifier = ?1 then transferringAccountIdentifier "
        + "   when acquiringAccountIdentifier = ?1 then acquiringAccountIdentifier "
        + "end as identifier,"        
        + "case"
        + "   when transferringAccountIdentifier = ?1 then transferringAccountBalance "
        + "   when acquiringAccountIdentifier = ?1 then acquiringAccountBalance "
        + "   else 0 "
        + "end as balance,"
        + "case"
        + "   when transferringAccountIdentifier = ?1 then transferringAccountBalanceUnitType "
        + "   when acquiringAccountIdentifier = ?1 then acquiringAccountBalanceUnitType "
        + "end as unitType) "
        + "from TransactionAccountBalance "
        + "where (transferringAccountIdentifier = ?1 or acquiringAccountIdentifier = ?1)"
        + "and lastUpdated = (select max(tab2.lastUpdated) from TransactionAccountBalance tab2 where (tab2.transferringAccountIdentifier = ?1 or tab2.acquiringAccountIdentifier = ?1) and tab2.lastUpdated < ?2)")
    Optional<AccountBalance> findByAccountIdentifierAndDate(Long accountIdentifier,Date balanceDateTime);
}
