package gov.uk.ets.registry.api.allocation.domain;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation phase.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"code"})
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
     * The allocation periods.
     */
    @OneToMany(mappedBy = "phase", fetch = FetchType.LAZY)
    private List<AllocationPeriod> periods;

}
