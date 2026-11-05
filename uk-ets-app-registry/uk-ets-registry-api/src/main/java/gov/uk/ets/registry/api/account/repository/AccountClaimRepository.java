package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountClaim;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountClaimRepository extends JpaRepository<AccountClaim, Long> {

    Optional<AccountClaim> findByRequestId(Long requestId);
}
