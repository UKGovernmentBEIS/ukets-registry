package gov.uk.ets.registry.api.compliance.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the exclusion status of an operator's emissions for a specific year.
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"compliantEntityId", "year", "excluded"})
public class ExcludeEmissionsEntry {
    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "exclude_emissions_entry_generator", sequenceName = "exclude_emissions_entry_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exclude_emissions_entry_generator")
    private Long id;
    
    /**
     * The compliant entity (installation / aircraft operator).
     */
    @Column(name = "compliant_entity_id")
    private Long compliantEntityId;

    /**
     * The year of the emission.
     */
    private Long year;

    /**
     * The exclusion status.
     */
    private boolean excluded;
    
    /**
     * When this entry was last updated.
     */
    @Column(name = "last_updated")
    private Date lastUpdated;
}
