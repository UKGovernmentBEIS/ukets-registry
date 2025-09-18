package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.AccountBalanceHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountBalanceHistoryRepository extends JpaRepository<AccountBalanceHistory, Long>, QuerydslPredicateExecutor<AccountBalanceHistory> {

    
    Optional<AccountBalanceHistory> findByAccountIdentifier(Long accountIdentifier);
    
}
