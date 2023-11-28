package gov.uk.ets.registry.api.transaction.domain;


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

import com.fasterxml.jackson.annotation.JsonBackReference;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores connections between transaction.
 */
@Entity
@Getter
@Setter
public class TransactionConnection implements Serializable {
	
	private static final long serialVersionUID = -3491168156424782400L;

	/**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_connection_id_generator", sequenceName = "transaction_connection_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_connection_id_generator")
    private Long id;

    /**
     * The left part of the connection (acts as the subject).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_transaction_id")
    @JsonBackReference(value="subject-transaction")
    private Transaction subjectTransaction;

    /**
     * The right part of the connection (acts as the object).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_transaction_id")
    @JsonBackReference(value="object-transaction")
    private Transaction objectTransaction;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private TransactionConnectionType type;

    /**
     * When this record was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

}
