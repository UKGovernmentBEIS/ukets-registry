package uk.gov.ets.transaction.log.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation phase.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = { "code" })
public class AllocationPhase {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_phase_generator", sequenceName = "allocation_phase_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_phase_generator")
    private Long id;

    /**
     * The code.
     */
    private Integer code;

    /**
     * The initial phase cap.
     */
    private Long initialPhaseCap;

    /**
     * The consumed phase cap (issued).
     */
    private Long consumedPhaseCap;

    /**
     * The pending phase cap.
     */
    private Long pendingPhaseCap;

    /**
     * The first year of the phase.
     */
    private Integer firstYear;

    /**
     * The last year of the phase.
     */
    private Integer lastYear;

}
