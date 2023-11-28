package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Installation;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Data repository for installations.
 */
public interface InstallationRepository extends JpaRepository<Installation, Long> {

    @Query(value = "select i.identifier from Installation i")
    Set<String> findAllInstallations();

    @Query("select id from Installation where identifier = ?1")
    Long getCompliantEntityId(Long identifier);

}
