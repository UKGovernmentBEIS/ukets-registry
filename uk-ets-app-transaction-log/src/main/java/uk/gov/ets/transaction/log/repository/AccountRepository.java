package uk.gov.ets.transaction.log.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import uk.gov.ets.transaction.log.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    Optional<Account> findByIdentifier(long identifier);
}
