package gov.uk.ets.registry.api.user.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamUserRoleRepository extends JpaRepository<IamUserRole, Long> {

    Optional<IamUserRole> findByIamIdentifier(String iamIdentifier);
}
