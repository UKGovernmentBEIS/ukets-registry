package gov.uk.ets.registry.api.file.upload.emissionstable.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an emission entry.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"compliantEntityId", "year", "uploadDate"})
@ToString
public class EmissionsEntry {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "emissions_entry_generator", sequenceName = "emissions_entry_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emissions_entry_generator")
    private Long id;

    /**
     * The name of the excel source file.
     */
    private String filename;

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
     * The verified emissions value.
     */
    private Long emissions;

    /**
     * The upload date.
     */
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
}
