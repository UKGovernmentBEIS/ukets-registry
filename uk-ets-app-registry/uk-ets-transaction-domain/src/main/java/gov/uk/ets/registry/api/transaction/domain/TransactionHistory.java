package gov.uk.ets.registry.api.transaction.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the various statuses of the transaction.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"transaction", "status"})
public class TransactionHistory implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 6863478372118731761L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_history_id_generator", sequenceName = "transaction_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_history_id_generator")
    private Long id;

    /**
     * The transaction status.
     */
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    /**
     * When the transaction status changed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * The transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @JsonBackReference
    private Transaction transaction;

}
