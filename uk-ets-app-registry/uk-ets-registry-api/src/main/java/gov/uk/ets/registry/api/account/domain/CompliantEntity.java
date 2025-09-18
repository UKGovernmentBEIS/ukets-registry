package gov.uk.ets.registry.api.account.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a compliant entity (e.g. an installation, aircraft operator).
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "identifier")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "compliant_entity_id_generator", sequenceName = "compliant_entity_seq", allocationSize = 1)
@Table(name = "compliant_entity")
public abstract class CompliantEntity implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2368806018265460491L;

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compliant_entity_id_generator")
    private Long id;

    /**
     * The end year.
     */
    @Column(name = "end_year")
    private Integer endYear;

    /**
     * The has been compliant.
     */
    @Column(name = "has_been_compliant")
    private Boolean hasBeenCompliant;

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The emitter id.
     */
    private String emitterId;

    /**
     * The start year.
     */
    @Column(name = "start_year")
    private Integer startYear;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    
    /**
     * The account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
    
    /**
     * The static status
     */
    @OneToMany(mappedBy = "compliantEntity", fetch = FetchType.LAZY)
    private List<StaticComplianceStatus> staticStatuses;

    /**
     * The regulator.
     */
    @Enumerated(EnumType.STRING)
    private RegulatorType regulator;

    /**
     * The changed regulator.
     */
    @Enumerated(EnumType.STRING)
    private RegulatorType changedRegulator;

    /**
     * The allocation classification.
     */
    @Enumerated(EnumType.STRING)
    private AllocationClassification allocationClassification;

    /**
     * The allocation withhold status.
     */
    @Enumerated(EnumType.STRING)
    private AllocationStatusType allocationWithholdStatus;
}
