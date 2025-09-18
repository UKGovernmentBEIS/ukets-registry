package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a block.
 */
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(of = {"startBlock", "endBlock", "originatingCountryCode"})
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
     * The originating country code.
     */
    @Column(name = "originating_country_code")
    private String originatingCountryCode;

    /**
     * The original commitment period.
     */
    @Column(name = "original_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod originalPeriod;

    /**
     * The applicable commitment period.
     */
    @Column(name = "applicable_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod applicablePeriod;

    /**
     * The environmental LULUCF activity.
     */
    @Column(name = "environmental_activity")
    @Enumerated(EnumType.STRING)
    private EnvironmentalActivity environmentalActivity;

    /**
     * The expiry date.
     */
    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    /**
     * Whether this block is subject to SOP.
     */
    @Column(name = "sop")
    private Boolean subjectToSop;

    /**
     * The JI project number.
     */
    @Column(name = "project_number")
    private String projectNumber;

    /**
     * The JI project track.
     */
    @Column(name = "project_track")
    @Enumerated(EnumType.STRING)
    private ProjectTrack projectTrack;

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

}
