package gov.uk.ets.registry.api.allocation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This is for testing reasons
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"compliantEntityId", "year", "excluded"})
public class ExcludeEmissionsEntry {

    @Id
    @GeneratedValue
    private Long id;
    private Long compliantEntityId;
    private Long year;
    private boolean excluded;

}
