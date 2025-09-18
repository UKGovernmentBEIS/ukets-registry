package uk.gov.ets.transaction.log.domain;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"transaction", "errorCode", "transactionBlockId", "dateOccurred"})
public class TransactionResponse implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -6863478372118731544L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_response_id_generator", sequenceName = "transaction_response_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_response_id_generator")
    private Long id;

    @Column(name = "error_code")
    private Long errorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(name = "details")
    private String details;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_occurred")
    private Date dateOccurred;

    @Column(name = "transaction_block_id")
    private Long transactionBlockId;

}