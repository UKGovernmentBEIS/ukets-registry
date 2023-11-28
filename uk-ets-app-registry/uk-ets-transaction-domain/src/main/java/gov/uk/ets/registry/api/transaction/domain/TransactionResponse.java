package gov.uk.ets.registry.api.transaction.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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