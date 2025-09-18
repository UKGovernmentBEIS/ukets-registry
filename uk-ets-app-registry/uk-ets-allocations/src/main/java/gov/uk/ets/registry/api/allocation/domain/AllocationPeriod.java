package gov.uk.ets.registry.api.allocation.domain;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation period.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"code"})
public class AllocationPeriod {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_period_generator", sequenceName = "allocation_period_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_period_generator")
    private Long id;

    /**
     * The code.
     */
    private Integer code;

    /**
     * The allocation phase where this period belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_phase_id")
    private AllocationPhase phase;

    /**
     * The allocation years.
     */
    @OneToMany(mappedBy = "period", fetch = FetchType.LAZY)
    private List<AllocationYear> years;

}
