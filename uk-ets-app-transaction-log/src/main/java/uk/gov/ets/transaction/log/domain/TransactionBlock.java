package uk.gov.ets.transaction.log.domain;

import javax.persistence.Entity;
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

/**
 * Represents a transaction block.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TransactionBlock extends Block {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -1767178086658528504L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_block_id_generator", sequenceName = "transaction_block_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_block_id_generator")
    private Long id;

    /**
     * The transaction where these blocks belong to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

}
