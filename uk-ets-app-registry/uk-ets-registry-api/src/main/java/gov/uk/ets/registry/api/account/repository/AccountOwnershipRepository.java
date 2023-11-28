package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOwnershipRepository extends JpaRepository<AccountOwnership, Long> {

    List<AccountOwnership> findByAccountAndHolderAndStatus(Account account, AccountHolder holder,
                                                           AccountOwnershipStatus status);
}
