package gov.uk.ets.registry.api.user.repository;

import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {
}
