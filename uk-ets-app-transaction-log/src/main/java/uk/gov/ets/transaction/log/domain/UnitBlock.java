package uk.gov.ets.transaction.log.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a unit block.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper= true)
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

}
