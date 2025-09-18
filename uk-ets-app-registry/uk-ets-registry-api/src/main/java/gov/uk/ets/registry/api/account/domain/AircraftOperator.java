package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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
     * The monitoring plan identifier.
     */
    @Column(name = "monitoring_plan_identifier")
    @Convert(converter = StringTrimConverter.class)
    private String monitoringPlanIdentifier;

}