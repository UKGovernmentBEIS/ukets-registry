package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.RegistryLevel;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for entitlements.
 */
public interface EntitlementRepository extends JpaRepository<RegistryLevel, Long> {

    /**
     * Retrieves the entitlement based on its type.
     * @param type The entitlement type.
     * @return an entitlement.
     */
    RegistryLevel findByType(RegistryLevelType type);

}
