package gov.uk.ets.registry.api.user.profile.recovery.repository;

import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityCodeRepository extends JpaRepository<SecurityCode, Long> {

    Optional<SecurityCode> findByUserAndValidIsTrue(User user);
    long countByUser(User user);
    void deleteAllByUser(User user);
}
