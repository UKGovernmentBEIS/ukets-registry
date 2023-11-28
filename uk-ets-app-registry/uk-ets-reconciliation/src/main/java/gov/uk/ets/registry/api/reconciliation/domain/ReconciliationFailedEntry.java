package gov.uk.ets.registry.api.reconciliation.domain;

import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"reconciliation", "accountIdentifier"})
public class ReconciliationFailedEntry implements Serializable {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "reconciliation_generator", sequenceName = "reconciliation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_generator")
    private Long id;

    /**
     * The reconciliation where this entry belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciliation_id")
    private Reconciliation reconciliation;

    /**
     * The account business identifier.
     */
    private Long accountIdentifier;

    @Column(name="unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    /**
     * The quantity calculated in the Registry.
     */
    private Long quantityRegistry;

    /**
     * The quantity calculated in the transaction log.
     */
    private Long quantityTransactionLog;
}
