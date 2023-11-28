package gov.uk.ets.registry.api.transaction.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionProtocol;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionSystem;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the various messages exchanged during a transaction.
 */
@Entity
@Getter
@Setter
public class TransactionMessage {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_message_id_generator", sequenceName = "transaction_message_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_message_id_generator")
    private Long id;

    /**
     * The message.
     */
    private String message;

    /**
     * The system that sent this message (e.g. ITL, TL).
     */
    @Enumerated(EnumType.STRING)
    private TransactionSystem sender;

    /**
     * The system that received this message.
     */
    @Enumerated(EnumType.STRING)
    private TransactionSystem recipient;

    /**
     * The protocol (e.g. Kyoto, ETS).
     */
    @Enumerated(EnumType.STRING)
    private TransactionProtocol protocol;

    /**
     * The transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @JsonBackReference
    private Transaction transaction;

    /**
     * When this record was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * The data calculated during this reconciliation.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(
        columnDefinition = "TEXT"
    )
    private String payload;
}
