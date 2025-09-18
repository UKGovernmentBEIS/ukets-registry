package gov.uk.ets.registry.api.allocation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation year.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"year"})
public class AllocationYear {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_year_generator", sequenceName = "allocation_year_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_year_generator")
    private Long id;

    /**
     * The year.
     */
    private Integer year;

    /**
     * The initial yearly cap.
     */
    private Long initialYearlyCap;

    /**
     * The consumed yearly cap (issued).
     */
    private Long consumedYearlyCap;

    /**
     * The pending yearly cap.
     */
    private Long pendingYearlyCap;

    /**
     * The yearly planned quantity (entitlement).
     */
    @Column(name = "entitlement")
    private Long entitlementYearly;

    /**
     * The yearly allocated quantity.
     */
    @Column(name = "allocated")
    private Long allocatedYearly;

    /**
     * The yearly auctioned quantity.
     */
    @Column(name = "auctioned")
    private Long auctionedYearly;

    /**
     * The yearly quantity returned via return of excess allocations.
     */
    @Column(name = "returned")
    private Long returnedYearly;

    /**
     * The yearly quantity returned via allocation reversals.
     */
    @Column(name = "reversed")
    private Long reversedYearly;

    /**
     * The allocation period of this year.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_period_id")
    private AllocationPeriod period;

    /**
     * The allocation period of this year.
     */
    @OneToMany(mappedBy = "allocationYear", fetch = FetchType.LAZY)
    private List<AllocationEntry> allocationEntries;

    /**
     * The account allocation status.
     */
    @OneToMany(mappedBy = "allocationYear", fetch = FetchType.LAZY)
    private List<AllocationStatus> allocationStatuses;

}
