package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.PermitStatus;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import java.util.Date;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an aircraft operator.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "compliantEntity")
@Table(name = "aircraft_operator")
@PrimaryKeyJoinColumn(name = "compliant_entity_id")
public class AircraftOperator extends CompliantEntity {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -4587008853696270300L;

    /**
     * The monitoring plan expiry date.
     */
    @Column(name = "monitoring_plan_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date monitoringPlanExpiryDate;

    /**
     * The monitoring plan identifier.
     */
    @Column(name = "monitoring_plan_identifier")
    @Convert(converter = StringTrimConverter.class)
    private String monitoringPlanIdentifier;

    /**
     * The permit status.
     */
    @Column(name = "permit_status")
    @Enumerated(EnumType.STRING)
    private PermitStatus permitStatus;

}