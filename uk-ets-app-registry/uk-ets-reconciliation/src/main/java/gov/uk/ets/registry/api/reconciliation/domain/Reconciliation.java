package gov.uk.ets.registry.api.reconciliation.domain;

import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an allocation job.
 */
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"identifier"})
public class Reconciliation implements Serializable {
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2485249781074668338L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "reconciliation_generator", sequenceName = "reconciliation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_generator")
    private Long id;

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    private ReconciliationStatus status;

    /**
     * The date when the job was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /**
     * The date when it was last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * The data calculated during this reconciliation.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(
        columnDefinition = "TEXT"
    )
    private String data;

    /**
     * The entries that were marked as failed during this reconciliation.
     */
    @OneToMany(mappedBy = "reconciliation", fetch = FetchType.LAZY)
    private List<ReconciliationFailedEntry> failedEntries;

    /**
     * The history entries of this reconciliation.
     */
    @OneToMany(mappedBy = "reconciliation", fetch = FetchType.LAZY)
    private List<ReconciliationHistory> historyEntries;
}
