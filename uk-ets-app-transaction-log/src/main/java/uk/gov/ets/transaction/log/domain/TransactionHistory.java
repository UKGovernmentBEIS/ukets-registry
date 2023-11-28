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
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;

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
    private Transaction transaction;

}
