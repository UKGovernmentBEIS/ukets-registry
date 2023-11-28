package gov.uk.ets.registry.api.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import gov.uk.ets.registry.api.transaction.domain.AccountBalanceHistory;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;

public interface AccountBalanceHistoryRepository extends JpaRepository<AccountBalanceHistory, Long>, QuerydslPredicateExecutor<AccountBalanceHistory> {

    
    Optional<AccountBalanceHistory> findByAccountIdentifier(Long accountIdentifier);
    
    
    /**
     * Updates the account balance history.
     *
     * @param identifier The account identifier.
     * @param balance    The balance.
     * @param unitType   The unit type (e.g. Multiple).
     */
    @Modifying
    @Query("update AccountBalanceHistory set balance = ?2, unitType = ?3 , lastTransactionIdentifier = ?4 where identifier = ?1")
    void updateAccountBalanceHistory(Long identifier, Long balance, UnitType unitType,String transactionIdentifier);
    
}
