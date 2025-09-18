package gov.uk.ets.registry.api.transaction.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a unit block.
 */
@Entity
@Getter
@Setter
public class UnitBlock extends Block {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 613254776335978526L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "unit_block_id_generator", sequenceName = "unit_block_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_block_id_generator")
    private Long id;

    /**
     * The unique business identifier of the account that currently holds this unit block.
     */
    @Column(name = "account_identifier")
    private Long accountIdentifier;

    /**
     * The transaction unique business identifier which has reserved this unit block.
     */
    @Column(name = "reserved_for_transaction")
    private String reservedForTransaction;

    /**
     * When the unit block was acquired by the account.
     */
    @Column(name = "acquisition_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquisitionDate;

    /**
     * When the unit block was acquired by the account.
     */
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * This flag will identify units which have been replaced during a Replacement transaction.
     * This flag should be set to true during the finalisation of a Replacement transaction.
     */
    @Column(name = "replaced")
    private Boolean replaced;

    /**
     * Stores the identifier of the Replacement transaction for which these units are marked as candidates to be replaced.
     * This field should be set upon unit reservation during a transaction proposal.
     */
    @Column(name = "reserved_for_replacement")
    private String reservedForReplacement;
}
