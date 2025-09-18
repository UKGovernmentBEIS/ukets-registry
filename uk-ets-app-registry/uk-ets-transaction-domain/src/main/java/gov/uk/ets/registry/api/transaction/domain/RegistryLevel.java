package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the levels, limits and entitlements for the registry.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"type", "originalPeriod", "applicablePeriod"})
public class RegistryLevel {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "registry_total_id_generator", sequenceName = "registry_level_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registry_total_id_generator")
    private Long id;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private RegistryLevelType type;

    /**
     * The unit type.
     */
    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    /**
     * The original commitment period.
     */
    @Column(name = "commitment_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod period;

    /**
     * The environmental LULUCF activity.
     */
    @Column(name = "environmental_activity")
    @Enumerated(EnumType.STRING)
    private EnvironmentalActivity environmentalActivity;

    /**
     * The initial value (e.g. issuance limit).
     */
    @Column(name = "initial")
    private Long initialQuantity;

    /**
     * The total consumed value (e.g. issued quantity).
     */
    @Column(name = "consumed")
    private Long consumedQuantity;

    /**
     * The pending value (e.g. reserved issued units pending to be approved)..
     */
    @Column(name = "pending")
    private Long pendingQuantity;

}
