package gov.uk.ets.registry.api.common.security;

import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data repository for used tokens.
 */
public interface UsedTokenRepository extends JpaRepository<UsedToken, Long> {

    Optional<UsedToken> findByToken(String token);

    int deleteAllByExpiredAtBefore(Date date);
}
