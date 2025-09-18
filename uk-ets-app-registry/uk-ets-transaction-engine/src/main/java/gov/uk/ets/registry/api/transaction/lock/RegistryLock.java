package gov.uk.ets.registry.api.transaction.lock;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The Registry Lock entity
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
public class RegistryLock {

    /**
     * The identifier of the lock.
     */
    @Id
    private String key;
}
