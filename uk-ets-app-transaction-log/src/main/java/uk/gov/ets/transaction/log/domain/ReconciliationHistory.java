package uk.gov.ets.transaction.log.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.ReconciliationStatus;

/**
 * Stores the various statuses of the transaction.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"reconciliation", "status"})
public class ReconciliationHistory implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -873629934146131458L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "reconciliation_history_id_generator", sequenceName = "reconciliation_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_history_id_generator")
    private Long id;

    /**
     * The reconciliation status.
     */
    @Enumerated(EnumType.STRING)
    private ReconciliationStatus status;

    /**
     * When the reconciliation status changed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * The reconciliation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciliation_id")
    private Reconciliation reconciliation;

}
