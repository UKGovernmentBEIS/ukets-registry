package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Encapsulates the holding of an account.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"unitType", "originalPeriod", "applicablePeriod", "projectNumber", "projectTrack", "subjectToSop"})
public class AccountHolding implements Serializable {

    /**
     * The unit type.
     */
    private UnitType unitType;

    /**
     * The original commitment period.
     */
    private CommitmentPeriod originalPeriod;

    /**
     * The applicable commitment period.
     */
    private CommitmentPeriod applicablePeriod;

    /**
     * The project number.
     */
    private String projectNumber;

    /**
     * The project track.
     */
    private ProjectTrack projectTrack;

    /**
     * Whether this holding is subject to SOP.
     */
    private Boolean subjectToSop;

    /**
     * The available quantity.
     */
    private Long availableQuantity;

    /**
     * The reserved quantity.
     */
    private Long reservedQuantity;

    /**
     * Constructor.
     * @param unitType The unit type.
     * @param availableQuantity The available quantity.
     */
    public AccountHolding(UnitType unitType, Long availableQuantity) {
        this.unitType = unitType;
        this.availableQuantity = availableQuantity;
    }
}
