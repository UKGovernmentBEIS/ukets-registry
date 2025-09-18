package uk.gov.ets.transaction.log.domain;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.UnitType;

/**
 * Represents a block.
 */
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(of = {"startBlock", "endBlock"})
public abstract class Block implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 1924802498698952985L;

    /**
     * The start serial number of the unit block.
     */
    @Column(name = "start_block")
    private Long startBlock;

    /**
     * The end serial number of the unit block.
     */
    @Column(name = "end_block")
    private Long endBlock;

    /**
     * The unit type.
     */
    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType type;

    /**
     * The year (in commitment period, issuance etc.).
     */
    private Integer year;

    /**
     * Returns the quantity.
     * @return the quantity.
     */
    public Long getQuantity() {
        return endBlock - startBlock + 1;
    }

    /**
     * Checks if this unit block overlaps with the provided block.
     * A unit block overlaps with another block if <strong>both conditions hold true:</strong>:
     * <ol>
     * <li>The start block is less than or equal to the end of the other block</li>
     * <li>The end block is greater than or equal to the start of the other block</li>
     * </ol>
     * 
     * @param otherBlock the block to compare with
     * @return
     */
    public boolean isOverlapping(Block otherBlock) {
        return getStartBlock() <= otherBlock.getEndBlock() && getEndBlock() >= otherBlock.getStartBlock();
    }   
}
